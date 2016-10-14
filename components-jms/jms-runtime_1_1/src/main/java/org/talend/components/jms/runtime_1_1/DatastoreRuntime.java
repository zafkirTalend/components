package org.talend.components.jms.runtime_1_1;

public class DatastoreRuntime {/*
    List<NamedThing> getPossibleDatasetNames(RuntimeContainer container) throws IOException {
        // ajout dependence vers dataset pour avoir le type de destination
        List<NamedThing> datasetList = new ArrayList();
        try {
            Context context = new InitialContext();
            NamingEnumeration list = context.listBindings("");
            while (list.hasMore()) {
                Binding nc = (Binding) list.next();
                Object jmsObject = context.lookup(nc.getName());
                if (messageType.equals("topic") && jmsObject instanceof Topic) {
                    datasetList.add(new SimpleNamedThing(nc.getName(),nc.getName()));
                } else if (messageType.equals("queue") && jmsObject instanceof Queue) {
                    datasetList.add(new SimpleNamedThing(nc.getName(),nc.getName()));
                }
            }
        }catch (NamingException e) {
            e.printStackTrace();
        }
        return datasetList;
    }



*/
}
