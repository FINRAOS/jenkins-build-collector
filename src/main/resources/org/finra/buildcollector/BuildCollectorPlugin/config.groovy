package org.finra.buildcollector

import org.finra.buildcollector.AbstractBuildPersistor

def f = namespace(lib.FormTagLib)
def persistors = AbstractBuildPersistor.all()
def p_list =  it.getPersistors()


if(!persistors.isEmpty()){
	    f.section(title:_("Build Collector")) {
        f.block {
            f.hetero_list(name:"buildcollector", hasHeader:true, descriptors:AbstractBuildPersistor.all(), items:p_list,
                addCaption:_("Add a new Persistor"), deleteCaption:_("Delete Persistor"))
        }
    }
}

