package com.ibm.wala.inc;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.viz.DotUtil;
import com.ibm.wala.viz.NodeDecorator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class CallGraphPrinter {
  public static void print(String title, CallGraph cg, boolean simplified) {
    try {
    	System.err.println("output "+title);
      cg.iterator();
      Iterator<CGNode> cgIt = cg.iterator();
      HashMap<CGNode, String> labelMap = HashMapFactory.make();
      while (cgIt.hasNext()) {
        CGNode node = cgIt.next();
        if (simplified) {
          IMethod method = node.getMethod();
          String dklass = method.getDeclaringClass().getName().toString();
          dklass = dklass.substring(1, dklass.length());
          StringBuilder strBuilder = new StringBuilder(dklass);
          strBuilder.append(".");
          strBuilder.append(method.getName().toString());
          labelMap.put(node, strBuilder.toString());
        } else {
          labelMap.put(node, node.toString());
        }
      }
      NodeDecorator<CGNode> labels = labelMap::get;
      String fileName = title + "_callgraph";
      String dotFile = fileName + ".dot";
      String pdfFile = fileName = fileName + ".pdf";
      DotUtil.writeDotFile(cg, labels, fileName, dotFile);
      Runtime.getRuntime().exec(new String[] {"dot", "-Tpdf", dotFile, "-o", pdfFile});
    } catch (WalaException | IOException e) {
      throw new RuntimeException(e);
    }
  }
}
