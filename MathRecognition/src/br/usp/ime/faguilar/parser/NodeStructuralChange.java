/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.parser;

/**
 *
 * @author Frank
 */
public class NodeStructuralChange {
    public static final int CHANGE_TYPE_TO_SUPERSCRIPT = 0;
    public static final int CHANGE_TYPE_TO_SUBSCRIPT = 1;
    public static final int CHANGE_TYPE_TO_HORIZONTAL = 2;
    
    private RegionNode region;
    private SymbolNode symbolNode;
    private int type;
    private int regionLevel;
    
    public RegionNode getRegion() {
        return region;
    }

    public void setRegion(RegionNode region) {
        this.region = region;
    }

    public SymbolNode getSymbolNode() {
        return symbolNode;
    }

    public void setSymbolNode(SymbolNode symbolNode) {
        this.symbolNode = symbolNode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRegionLevel() {
        return regionLevel;
    }

    public void setRegionLevel(int regionLevel) {
        this.regionLevel = regionLevel;
    }    
}
