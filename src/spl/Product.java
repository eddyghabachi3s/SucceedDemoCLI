package spl;

import org.eclipse.jgit.revwalk.RevCommit;
import java.util.HashMap;

public class Product {
	
	private String name;
	private String description;
	private String path;
	private ProductFM fm;
	private RevCommit commit;
	private HashMap<Asset, AssetInstance> assetInstances;
	
	
	public Product(String name, String description, String path) {
		super();
		this.fm = new ProductFM();
		this.name = name;
		this.description = description;
		this.path = path;
		this.assetInstances = new HashMap<Asset, AssetInstance>();
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
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public ProductFM getFm() {
		return fm;
	}

	public void setFm(ProductFM fm) {
		this.fm = fm;
	}

	public RevCommit getCommit() {
		return commit;
	}

	public void setCommit(RevCommit commit) {
		this.commit = commit;
	}

	public HashMap<Asset, AssetInstance> getAssetInstances() {
		return assetInstances;
	}

	public void setAssetInstances(HashMap<Asset, AssetInstance> assetInstances) {
		this.assetInstances = assetInstances;
	}

}
