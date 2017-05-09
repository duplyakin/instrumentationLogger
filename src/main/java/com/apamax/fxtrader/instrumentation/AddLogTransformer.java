package com.apamax.fxtrader.instrumentation;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import org.apache.log4j.Logger;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Vlad on 08.05.2017.
 */
public class AddLogTransformer implements ClassFileTransformer {


       // public HashMap< String, HashSet< String >> mInstrumentedMethods;

        private final Class<?> actionListener;


        public AddLogTransformer() {
          //  mInstrumentedMethods = new HashMap< String, HashSet< String > >();

         //   mInstrumentedMethods.put( "java.util.Random", new HashSet< String >() );
         //   mInstrumentedMethods.get( "java.util.Random").add( "nextLong" );
            actionListener= ActionListener.class;
        }

        @Override
        public byte[] transform(
                ClassLoader       loader,
                String            className,
                Class<?>          classBeingRedefined,
                ProtectionDomain protectionDomain,
                byte[]            classfileBuffer) throws IllegalClassFormatException {

            System.err.println( "---- Instrumenting: " + className );
            byte[] byteCode = classfileBuffer;
           // String normalizedClassName = className.replaceAll("/", ".");

            if (actionListener.isAssignableFrom(classBeingRedefined)&&!classBeingRedefined.isInterface()) {
                try {
                    ClassPool cp = ClassPool.getDefault();
                    cp.importPackage( "org.apache.log4j");
                    CtClass cc = cp.makeClass( new java.io.ByteArrayInputStream( byteCode ) );
                    if(cc.getDeclaredField("logger")==null){
                        cc.addField(new CtField(cp.get(Logger.class.getCanonicalName()),"logger",cc),"Logger.getLogger("+cc.getSimpleName()+".class)");
                    }

                    //private static final Logger logger = Logger.getLogger(ColorChooser.class);
                    CtMethod m  = cc.getDeclaredMethod( "actionPerformed" );
                    //cc.addField();
                    StringBuilder sbs = new StringBuilder();
                    sbs.append( "Object source = $args[0].getSource();" );
                    sbs.append( "if(source instanceof JButton){" );
                    sbs.append( "  JButton btn = (JButton)source;" );
                    sbs.append( "  logger.warn (\"btn pressed! \"+btn.getName()" );
                    sbs.append( "}" );
                    m.insertBefore("{" + sbs.toString() + "}");
                    byteCode = cc.toBytecode();
                    cc.detach();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return byteCode;
        }
}
