/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DatabaseMathExpressions;

import java.io.Serializable;

/**
 *
 * @author frank.aguilar
 */
public class Category implements Serializable {
   
    private static final long serialVersionUID = 1L;
    private String name;
    private String description;

    public Category() {
        name = "";
        description = "";
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Category)) {
            return false;
        }
        Category other = (Category) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GUIs.InputOfExpressions.Category[ name=" + name + " ]";
    }    
}
