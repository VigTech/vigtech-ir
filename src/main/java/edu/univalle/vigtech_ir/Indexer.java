/*
 * To change this license header, choose License Headers in Project PropertiesManager.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.univalle.vigtech_ir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
//import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import edu.univalle.vigtech_ir.utils.Index;
import edu.univalle.vigtech_ir.utils.PropertiesManager;

/**
 *
 * @author olaya
 */
public class Indexer {

    private boolean DEBUG = false;

    static Set<String> textualMetadataFields //2
            = new HashSet<String>();                     //2

    static {                                           //2
        textualMetadataFields.add(Metadata.TITLE);       //2
        textualMetadataFields.add(Metadata.AUTHOR);      //2
        textualMetadataFields.add(Metadata.COMMENTS);    //2
        textualMetadataFields.add(Metadata.KEYWORDS);    //2
        textualMetadataFields.add(Metadata.DESCRIPTION); //2
        textualMetadataFields.add(Metadata.SUBJECT);     //2
    }

    // Método de indexacion principal, requiere directorio donde se crear los 
    // archivos de indexacion y el directorio que contiene los documentos
    //que seran indexados.
    public Index indexar(String indexPath, String docsPath) {
        System.out.println("Ruta de indices: " + indexPath);
        System.out.println("Ruta de documentos: " + docsPath);
        Index index = new Index();

        try {
        	
        	File indexDir = new File(indexPath);
        	
            //Abre directorio donde se guardaran indices.
            Directory dir = FSDirectory.open(indexDir);

            //genera la configuracion del IndexWriter, el cual contiene el analizador.
            IndexWriterConfig iwc = crearAnalyzer(indexPath);
            System.out.println(iwc.getOpenMode());
            //Genera el indexWriter con el directorio donde serán guardados los
            //indices y la configuracipn del mismo.
            IndexWriter iw = new IndexWriter(dir, iwc);

            //Directorio donde se encuentran los documentos a ser indexados.
            File docDir = new File(docsPath);

            //Añadir indexacion de documentos del directorio de documentos a indexar
            //utilizando el Indexwriter
            addDocuments(docDir, iw);
            index.setDocCount(iw.numDocs());
            index.setId(indexDir.getName());
            //Es necesario cerrar la instancia de indexWriter.
            iw.close();

        } catch (IOException e) {
            System.out.println("Error de tipo " + e.getClass() + "\n dice: " + e.getMessage());
            e.printStackTrace();
        }
		return index;
    }

    //Revisa si el directorio de indexacion contiene ya indices creados
    //anteriormente. True si ya contiene, false de lo contrario.
    private boolean hasOldIndex(String indexDir) {
        boolean hasOldIndexFiles = false;
        File[] files = new File(indexDir).listFiles();

        if (files.length > 1) {
            hasOldIndexFiles = true;
        }
        return hasOldIndexFiles;
    }

    //Genera el tipo de analyzer que sera utilizado el cual por default sera
    //el standardAnalyzer. Desde versiones recientes el analizador
    //esta contenido en el IndexWriterConfig, que contiene informacion
    //sobre la verson siendo utilizada, el analizador y en este caso si es
    //necesario sobreescribir el archivo de indexacion.
    private IndexWriterConfig crearAnalyzer(String indexPath) {
        //Crea el Analizador espcificando que es un analizador estandar
        //de la version 4.9 de Lucene.
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);

        //Configuracion del IndexWriter de la version 4.9 de lucene utilizando el
        //analizador espcificado.
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);

        boolean has_index = hasOldIndex(indexPath);

        System.out.println("Existe indice antiguo? " + has_index);

        //Si el directorio de indices contiene indices, los sobreescribe (temporal).
        //Sino, muestra el mensaje correspondiente.
        if (has_index) {
            iwc.setOpenMode(OpenMode.APPEND);
            System.out.println("No toco crear indice nuevo.");
        } else {
            iwc.setOpenMode(OpenMode.CREATE);
            System.out.println("Tocó crear indice nuevo.");
        }

        return iwc;
    }

    //addDocuments busca indexar todos los documentos del directorio de archivos
    //a ser indexados y utiliza el indexWriter para lograrlo. 
    private void addDocuments(File docDir, IndexWriter iw) {
        try {

            //Verifica si el directorio de docuimentos a indexar se puede leer y
            //si es directorio.
            if (docDir.canRead() && docDir.isDirectory()) {

                //Si listan todos los documentos del directorio.
                File[] docFiles = docDir.listFiles();

                //Revisa que la lista no sea nula.
                if (docFiles != null) {

                    //Genera un id para el documento y recorre cada archivo.
                    int docID = 0;
                    for (int i = 0; i < docFiles.length; i++) {

                        //Si uno de los elementos del directorio es un Directorio
                        //se procede a realizar el procedimeinto addDocuments
                        //a este.
                        if (docFiles[i].isDirectory()) {
                            File docDirNew = new File(docFiles[i].getCanonicalPath());
                            addDocuments(docDirNew, iw);
                        } else {
                            //Sino es directorio se procede a leer la informacion
                            //contenida dentro del archivo. Se utiliza el lector
                            //de archivos apache tika.
                            String texto = TikaDocReader(docFiles[i]);
                            
                            String fileName = docFiles[i].getName().toLowerCase();
                            if (texto != null && !fileName.endsWith(".xml")
                            		&& !fileName.contentEquals("docs.txt")
                            		&& !fileName.endsWith(".json")) {

                                //Si el archivo contiene informacion se añade la
                                //informacion una instancia de documento de
                                //apache lucene llamado Document.
                                String id = docID++ + "";
                                Document doc = addDocument(docFiles[i], texto, id);

                                //Finalmente se añade el Document al IndexWriter
                                //para ser indexado.
                                iw.addDocument(doc);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error de tipo " + e.getClass() + "\n dice: " + e.getMessage());
        }

    }

    //TikaDocReader se utiliza para poder indexar diferentes tipos de documentos
    //(pdf, doc, ppt, etc) y pasarlos a texto plano
    private String TikaDocReader(File docFile) {

        try {

            Metadata metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, docFile.getName());

            InputStream is = new FileInputStream(docFile);      // 5
            Parser parser = new AutoDetectParser();       // 6
            ContentHandler handler = new BodyContentHandler(1000000); // Se puede agregar un entero grande para aumentar la capacidad   Ejemplo new BodyContentHandler(1000000)

            ParseContext context = new ParseContext();   // 8
            context.set(Parser.class, parser);           // 8

            try {
                parser.parse(is, handler, metadata, // 9
                        new ParseContext());        // 9
            } catch (SAXException ex) {
                Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TikaException ex) {
                Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                is.close();
            }

            String texto = handler.toString();

            return texto;
        } catch (IOException e) {
            System.out.println("Error de tipo " + e.getClass() + "\n dice: " + e.getMessage());
        }
        return null;
    }

    //No se usa ya que Tika se encarga de todo tipo de documentos.
    private String SimpleDocReader(File docFile) {
        String texto = "";
        try {

            if (docFile.getName().endsWith(".txt")) {

                BufferedReader br = new BufferedReader(new FileReader(docFile));

                try {
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();

                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = br.readLine();
                    }

                    texto += sb.toString();
                } finally {
                    br.close();
                }
            }
            return texto;
        } catch (IOException e) {
            System.out.println("Error de tipo " + e.getClass() + "\n dice: " + e.getMessage());
        }
        return null;
    }

    //Lucene almacena la informacion de cada documento en su propia instancia de
    //documento llamado Document. En este se añaden diferentes Campos
    //representativos del texto, en este caso id, ruta del documento, titulo y
    //contenido.
    private Document addDocument(File docFile, String texto, String id) {

        Document doc = new Document();

        //Se añade el id del documento
        doc.add(new StringField("id", id, Field.Store.YES));
        System.out.println("Se indexo el documento con id: " + doc.get("id"));

        //Se añade la direccion del documento
        doc.add(new StringField("path", docFile.getPath(), Field.Store.YES));
        System.out.println("Se indexo el documento con ruta: " + doc.get("path"));

        //Se añade el titulo del documento
        doc.add(new StringField("title", docFile.getName().split(".pdf")[0], Field.Store.YES));
        System.out.println("Se indexo el documento con titulo: " + doc.get("title"));

        //Se añade el contenido del documento
        doc.add(new TextField("contents", texto /*new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))*/, Field.Store.YES));
        System.out.println("Se indexo el contenido del documento");

        return doc;

    }
    
	public Index getIndex(String id){
	      
        Index index = new Index();

        try {
        	
        	String indexPath = PropertiesManager.getInstance().getProperty("index.path")+id;
        	
        	File indexDir = new File(indexPath);
        	
            //Abre directorio donde se guardaran indices.
            Directory dir = FSDirectory.open(indexDir);

            //genera la configuracion del IndexWriter, el cual contiene el analizador.
            IndexWriterConfig iwc = crearAnalyzer(indexPath);
            System.out.println(iwc.getOpenMode());
            //Genera el indexWriter con el directorio donde serán guardados los
            //indices y la configuracipn del mismo.
            IndexWriter iw = new IndexWriter(dir, iwc);

            index.setDocCount(iw.numDocs());
            index.setId(indexDir.getName());
            //Es necesario cerrar la instancia de indexWriter.
            iw.close();

        } catch (IOException e) {
            System.out.println("Error de tipo " + e.getClass() + "\n dice: " + e.getMessage());
            e.printStackTrace();
        }
		return index;
    }


    public static void main(String args[]) {
        Indexer i = new Indexer();
        if(args.length == 2){
        String indexPath = args[0];
        String docsPath = args[1];
        i.indexar(indexPath, docsPath);
        } else{
            System.out.println("Requiere dos valores para poder ejecutarse");
        }
        
    }

}
