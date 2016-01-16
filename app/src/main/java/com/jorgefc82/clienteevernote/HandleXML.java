package com.jorgefc82.clienteevernote;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by Jorgefc82 on 14/01/2016.
 */
public class HandleXML {
    private String TAG_PARSEADOR = "ParseadorXML";
    private String descripcion = "";
    private String xml;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parseadoCompleto = true;

    //Constructor recibe el XML a parsear
    public HandleXML(String xml){
        this.xml = xml;
    }

    public String getDescripcion(){
        return descripcion;
    }

    /*Método que se encargará de almacenar el contenido cuando encuente la etiqueta que se busca*/
    public void parseaXMLyAlmacena(XmlPullParser myParser) {
        int event;
        String text="";
        String temporal="";

        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                    //cuando se encuentra etiqueta se guarda valor en variable
                        if(name.equals("div")) {
                            /*Se controla no duplicar texto con variable temporal y se añade
                            salto de linea cada vez que se encuentra una etiqueta div
                             */
                            if (!temporal.equals(text)){
                                temporal = text;
                             /*Se controla si se está en la primera línea para no
                                introducir un salto de línea de inicio*/
                                if (descripcion.equals("")){
                                    descripcion=temporal;
                                }else {
                                    descripcion = descripcion + "\n" + temporal;
                                }
                            }
                        }else{
                    /* Se crea esta condición porque se ha comprobado que las notas guardadas
                    * desde la app no van entre divs, de la manera que si no hay nada en la variable
                    * descripción al llegar a la etiqueta en-note, solo se debe recoger el contenido
                    * de dicha etiqueta*/
                            if (name.equals("en-note")){
                                if(descripcion.equals("")){
                                    descripcion =text;
                                }
                            }
                        }
                        break;
                }
                event = myParser.next();
            }
            parseadoCompleto = false;
        }
        catch (Exception e) {
            Log.e(TAG_PARSEADOR, "Error al parsear el XML");
            e.printStackTrace();
        }
    }

    /*Método que prepara en un hilo la cadena xml para ser parseada*/
    public void fetchXML(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    //Convierte string en inputstream
                    InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser parseador = xmlFactoryObject.newPullParser();

                    parseador.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parseador.setInput(stream, null);

                    parseaXMLyAlmacena(parseador);
                    stream.close();
                }
                catch (Exception e) {
                    Log.e(TAG_PARSEADOR, "Error preparando el parseo");
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
