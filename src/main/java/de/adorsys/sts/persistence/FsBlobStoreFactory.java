package de.adorsys.sts.persistence;

import java.io.File;
import java.util.Properties;

import org.adorsys.encobject.service.BlobStoreContextFactory;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.filesystem.reference.FilesystemConstants;

public class FsBlobStoreFactory implements BlobStoreContextFactory {
	
	Properties properties = new Properties();
	
	private String baseDir;
	
	public FsBlobStoreFactory(String baseDir){
		this.baseDir = baseDir;
		properties.setProperty(FilesystemConstants.PROPERTY_BASEDIR, baseDir);
	}
	
	public boolean existsOnFs(String container, String name){
		File file = new File(baseDir + "/" + container + "/" + name );
		return file.exists();
	}

	@Override
	public BlobStoreContext alocate() {
		 return ContextBuilder.newBuilder("filesystem")
         .overrides(properties)
         .buildView(BlobStoreContext.class);
	}

	@Override
	public void dispose(BlobStoreContext blobStoreContext) {
		blobStoreContext.close();
	}
}
