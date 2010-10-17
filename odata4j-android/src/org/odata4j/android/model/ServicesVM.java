package org.odata4j.android.model;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.examples.ODataEndpoints;

public class ServicesVM {

    public List<ServiceVM> getServices(){
        List<ServiceVM> rt = new ArrayList<ServiceVM>();
        rt.add(new ServiceVM("Netflix",ODataEndpoints.NETFLIX));
        rt.add(new ServiceVM("Baseball Stats",ODataEndpoints.BASEBALL_STATS));
        
        rt.add(new ServiceVM("TechEd 2010",ODataEndpoints.TECH_ED));
        rt.add(new ServiceVM("TechEd Europe 2010",ODataEndpoints.EU_TECH_ED));
        rt.add(new ServiceVM("Mix 2010",ODataEndpoints.MIX10));
        rt.add(new ServiceVM("Pluralsight",ODataEndpoints.PLURALSIGHT));
        rt.add(new ServiceVM("Agilitrain",ODataEndpoints.AGILITRAIN));
        rt.add(new ServiceVM("Telerik TV",ODataEndpoints.TELERIK_TV));
        
        rt.add(new ServiceVM("Stack Overflow",ODataEndpoints.STACK_OVERFLOW));
        rt.add(new ServiceVM("odata.org",ODataEndpoints.ODATA_WEBSITE_DATA));
        rt.add(new ServiceVM("OGDI",ODataEndpoints.OGDI_DC));
        rt.add(new ServiceVM("World Cup",ODataEndpoints.WORLD_CUP));
        rt.add(new ServiceVM("Northwind",ODataEndpoints.NORTHWIND));
       
        return rt;
    }
    
}
