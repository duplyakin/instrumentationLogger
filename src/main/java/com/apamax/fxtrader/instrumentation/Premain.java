package com.apamax.fxtrader.instrumentation;

import java.lang.instrument.Instrumentation;

/**
 * Created by Vlad on 08.05.2017.
 */
public class Premain {
       private static volatile Instrumentation instr;

        public static void premain(String agentArgs, Instrumentation inst) {
            instr = inst;
          //  SimpleClassTransformer transformer = new SimpleClassTransformer();
           // inst.addTransformer( transformer, false );
        }

}
