/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcrminer;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author rainzhang
 */
public class FPNode implements Comparable<FPNode>
{
    public String name;
    public int count;
    public FPNode parent;
    public List<FPNode> children;
    public FPNode nextItem;
    
    public FPNode () {}
    
    public FPNode (String Name)
    {
        this.name = Name;
    }

    public void addChild(FPNode child) 
    {
        if (this.children == null) 
        {
            List<FPNode> list = new ArrayList<>();
            list.add(child);
            this.children = list;
        } 
        else 
        {
            this.children.add(child);
        }
    }
  
    public FPNode findChild(String name) 
    {
        List<FPNode> children = this.children;
        if (children != null) 
        {
            for (FPNode child : children) 
            {
                if (child.name.equals(name)) 
                {
                    return child;
                }
            }
        }
        return null;
    }
  
    public void increaseCount() 
    {
        this.count ++;
    }

    
    public FPNode getParent() {
        return parent;
    }


    @Override
    public int compareTo(FPNode arg0) 
    {
        int count0 = arg0.count;
        return count0 - this.count;
    }
}
