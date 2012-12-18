/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.cost;

import br.usp.ime.faguilar.graph.Vertex;

/**
 *
 * @author Willian
 */
public class Cost {
     public float getCost(Vertex vm, Vertex vi){
         return vm.compareShapeContextExpression(vi);
     }
}
