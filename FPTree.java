package hcrminer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
/**
 *
 * @author rainzhang
 */
public class FPTree 
{
    public int minsup;
    public double minconf;
    public String option;
    
    public ArrayList<FPNode> Rank = new ArrayList<>();
    public ArrayList<FPNode> revRank = new ArrayList<>();
    public ArrayList<Container> Containers = new ArrayList<>();
    public Map<String, FPNode> Directory = new HashMap<>();
    
    public List<List<String>> getDB (String filename)
    {
        List<List<String>> transaction = null;

        transaction = new LinkedList<>();

        try
        {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            String line;
            List<String> record = new LinkedList<>();
            String flag = "0";

            while ((line = br.readLine()) != null)
            {
                if (line.length() > 0)
                {
                    String str[] = line.split(" ");

                    if (str[0].equals(flag))
                    {
                        record.add(str[1]);
                        if (!Directory.keySet().contains(str[1])) 
                        {
                            FPNode node = new FPNode(str[1]);
                            node.count = 1;
                            Directory.put(str[1], node);
                        } 
                        else 
                        {
                            Directory.get(str[1]).increaseCount();
                        }
                    }
                    else
                    {
                        flag = str[0];
                        transaction.add(record);
                        record = new LinkedList<>();
                        record.add(str[1]);
                        if (!Directory.keySet().contains(str[1])) 
                        {
                            FPNode node = new FPNode(str[1]);
                            node.count = 1;
                            Directory.put(str[1], node);
                        } 
                        else 
                        {
                            Directory.get(str[1]).increaseCount();
                        }
                    }
                }
            }
            br.close();

            Set<String> names = Directory.keySet();

            for (String name : names) 
            {
                FPNode tnode = Directory.get(name);
                if (tnode.count >= minsup) 
                {
                    Rank.add(tnode);
                    revRank.add(tnode);
                }
            }
            Collections.sort(Rank);
            Collections.sort(revRank);
            Collections.reverse(revRank);
        }
        catch (IOException ex)
        {
            System.out.println("Read transaction records failed."
                + ex.getMessage());
            System.exit(1);
        }
        
        return transaction;
    }
    
    public LinkedList<String> sortByOption(List<String> rec)
    {
        Map<String, Integer> map = new HashMap<>();
        
        if (option == "1")
        {
            LinkedList result = new LinkedList<>(rec);
            return result;
        }
        
        else if (option == "2")
        {
            for (String item : rec) 
            {
                for (int i = 0; i < revRank.size(); i++) 
                {
                    FPNode tnode = revRank.get(i);
                    if (tnode.name.equals(item)) 
                    {
                        map.put(item, i);
                    }
                }
            }
            
            ArrayList<Entry<String, Integer>> target = new ArrayList<>(map.entrySet());

            Collections.sort(target, new Comparator<Map.Entry<String, Integer>>() 
            {
                @Override
                public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1) 
                {
                    return arg0.getValue() - arg1.getValue();
                }
            });

            LinkedList<String> rest = new LinkedList<>();

            for (Entry<String, Integer> entry : target) 
            {
                rest.add(entry.getKey());
            }
            return rest;
        }
        
        else
        {
            for (String item : rec) 
            {
                for (int i = 0; i < Rank.size(); i++) 
                {
                    FPNode tnode = Rank.get(i);
                    if (tnode.name.equals(item)) 
                    {
                        map.put(item, i);
                    }
                }
            }

            ArrayList<Entry<String, Integer>> target = new ArrayList<>(map.entrySet());

            Collections.sort(target, new Comparator<Map.Entry<String, Integer>>() 
            {
                @Override
                public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1) 
                {
                    return arg0.getValue() - arg1.getValue();
                }
            });

            LinkedList<String> rest = new LinkedList<>();

            for (Entry<String, Integer> entry : target) 
            {
                rest.add(entry.getKey());
            }
            return rest;
        }
    }

    public void addNodes(FPNode p, LinkedList<String> t, ArrayList<FPNode> r)
    {
        while (t.size() > 0) 
        {
            String item = t.poll();
            FPNode nb = new FPNode(item);
            nb.count = 1;
            nb.parent = p;
            p.addChild(nb);

            for (FPNode f1 : r) 
            {
                if (f1.name.equals(item)) 
                {
                    while (f1.nextItem != null) 
                    {
                        f1 = f1.nextItem;
                    }
                    f1.nextItem = nb;
                    break;
                }
            }
            addNodes(nb, t, r);
        }
    }
    
    public FPNode buildFPTree(List<List<String>> rec, ArrayList<FPNode> r) 
    {
        FPNode root = new FPNode();
        
        for (List<String> transRecord : rec) 
        {
            
            FPNode temp = null;
            
            LinkedList<String> sorted = sortByOption(transRecord);
            FPNode s = root;
            
            if (root.children != null) 
            {
                while (!sorted.isEmpty() && (temp = s.findChild(sorted.peek())) != null) 
                {
                    temp.increaseCount();
                    s = temp;
                    sorted.poll();
                }
            }
            addNodes(s, sorted, r);
        }
        return root;
    }
    
    public void FPGrowth(List<List<String>> transRecords, List<String> tail) 
    {
        ArrayList<FPNode> r = getRank(transRecords);
        
        FPNode treeRoot = buildFPTree(transRecords, r);
        
        if (treeRoot.children == null || treeRoot.children.isEmpty())
            return;
        
        if(tail!=null)
        {
            for (FPNode header : r) 
            {
                Container c = new Container();
                c.Patterns.add(header.name);
                
                for (String ele : tail)
                    c.Patterns.add(ele);
                
                c.Count = header.count;
                
                Containers.add(c);
            }
        }
        
        for (FPNode header : r) 
        {
            List<String> nt = new LinkedList<>();
            nt.add(header.name);
            if (tail != null)
                nt.addAll(tail);
            
            List<List<String>> nr = new LinkedList<>();
            FPNode b = header.nextItem;
            while (b != null) 
            {
                int c = b.count;
                List<String> p = new ArrayList<>();
                FPNode po = b;
                
                while ((po = po.parent).name != null) 
                {
                    p.add(po.name);
                }
                
                while (c-- > 0) 
                {
                    nr.add(p);
                }
                b = b.nextItem;
            }
            FPGrowth(nr, nt);
        }
    }
    
    public Set<Set<String>> getSubset(Set<String> input)
    {
        String[] set = input.toArray(new String[input.size()]);
       
        Set<Set<String>> result = new HashSet<>();
        int length = set.length;
        int num = length==0 ? 0 : 1<<(length);
        
        for (int i = 0; i < num; i++)
        {
            Set<String> subset = new HashSet<>();
            int index = i;
            
            for (int j = 0; j < length; j++) 
            {
                if ((index & 1) == 1)
                {
                    subset.add(set[j]);
                }
                index >>=1 ;
            }
            if ((subset.size() != input.size()) && (subset.size() != 0))
            {
                result.add(subset);
            }
        }
        return result;
    }
    
    public void PrintResult(List<List<String>> rec, String outputfile)
    {
        try
        {
            File file = new File(outputfile);
            if (file.exists())
            {
                file.delete();
            }
            else 
            {
                file.createNewFile();
            }
            
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            ArrayList<Container> tempContainers = Containers;
            
            for (Container c : Containers)
            {
                Set<String> all = c.Patterns;
                Set<Set<String>> candidates = getSubset(all);
                
                for (Set<String> candidate : candidates)
                {
                    String content = "";
                    //print LHS
                    for (String s : candidate)
                    {
                        content += s + " ";
                    }
                    content += "| ";

                    Set<String> difference = new HashSet<>();
                    difference.addAll(all);
                    difference.removeAll(candidate);

                    //print RHS and support
                    for (String d : difference)
                    {
                        content += d + " ";
                    }
                    content += "| " + c.Count + " | ";
                    
                    //find confidence
                    int candidateCount = 0;
                    if (candidate.size() == 1)
                    {
                        candidateCount = Directory.get(candidate.iterator().next()).count;
                    }
                    else
                    {
                        for (Container test : tempContainers)
                        {
                            if (test.Patterns.equals(candidate))
                            {
                                candidateCount = test.Count;
                            }
                        }
                    }
                        
                    double conf = 0;
                    if (candidateCount == 0)
                    {
                        conf = -1;
                    }
                    else
                    {
                        conf = Math.round((double)c.Count/candidateCount * 1000);
                    }

                    if (conf/1000 >= minconf)
                    {
                        bw.write(content + (conf/1000));
                        bw.newLine();
                    }
                }
            }
            bw.close();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    public ArrayList<FPNode> getRank(List<List<String>> rec) 
    {
        ArrayList<FPNode> result = null;
        if (rec.size() > 0) 
        {
            result = new ArrayList<FPNode>();
            Map<String, FPNode> map = new HashMap<String, FPNode>();
            for (List<String> record : rec) 
            {
                for (String item : record) 
                {
                    if (!map.keySet().contains(item)) 
                    {
                        FPNode node = new FPNode(item);
                        node.count = 1;
                        map.put(item, node);
                    } 
                    
                    else 
                    {
                        map.get(item).increaseCount();
                    }
                }
            }
            Set<String> names = map.keySet();
            for (String name : names) 
            {
                FPNode tnode = map.get(name);
                if (tnode.count>= minsup) 
                {
                    result.add(tnode);
                }
            }
            Collections.sort(result);
            return result;
        } 
        else 
        {
            return null;
        }
    }
}
