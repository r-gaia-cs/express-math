/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification.neuralNetwork;

import org.neuroph.core.transfer.TransferFunction;

/**
 *
 * @author Frank Aguilar
 */
public class ExpTransferFunction extends TransferFunction{

    @Override
    public double getOutput(double net) {
//        System.out.println(net);
        if(net < -100)
            return 0;
        if(net > 100)
            return 2.6881171418161356E43;
        return Math.exp(net);
    }
    
}
