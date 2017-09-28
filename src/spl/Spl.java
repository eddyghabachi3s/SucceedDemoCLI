package spl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.google.common.collect.Sets;

public class Spl {

	private Scanner input;

	private String name;
	private String path;
	private Repository repository;
	private HashMap<String, Feature> features;
	private HashMap<String, Product> products;
	private HashMap<String, Asset> assets;
	private ArrayList<FeatureToAssetCorrelation> correlations;
	private ArrayList<FeatureToAssetInstanceCorrelation> instanceCorrelations;

	StringBuilder text = new StringBuilder();

	public Spl() {
		super();
		input = new Scanner(System.in);
		this.createSpl();
		// input.close();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public HashMap<String, Feature> getFeatures() {
		return features;
	}

	public void setFeatures(HashMap<String, Feature> features) {
		this.features = features;
	}

	public HashMap<String, Product> getProducts() {
		return products;
	}

	public void setProducts(HashMap<String, Product> products) {
		this.products = products;
	}

	public HashMap<String, Asset> getAssets() {
		return assets;
	}

	public void setAssets(HashMap<String, Asset> assets) {
		this.assets = assets;
	}

	public ArrayList<FeatureToAssetCorrelation> getCorrelations() {
		return correlations;
	}

	public void setCorrelations(ArrayList<FeatureToAssetCorrelation> correlations) {
		this.correlations = correlations;
	}

	public ArrayList<FeatureToAssetInstanceCorrelation> getInstanceCorrelations() {
		return instanceCorrelations;
	}

	public void setInstanceCorrelations(ArrayList<FeatureToAssetInstanceCorrelation> instanceCorrelations) {
		this.instanceCorrelations = instanceCorrelations;
	}

	private void createSpl() {
		input = new Scanner(System.in);
		System.out.print("Enter a name for the SPL you desire to create: ");
		name = input.nextLine();
		System.out.print("Enter a path under which your desire your SPL to be created: ");
		path = input.nextLine();
		if (path.lastIndexOf('/') != path.length() - 1) {
			path += "/";
		}
		createRepository();
		features = new HashMap<String, Feature>();
		products = new HashMap<String, Product>();
		assets = new HashMap<String, Asset>();
		inputProducts();
		constructFeatureToAssetCorrelations();
		computeCorrelationIndicators();
	}

	private void createRepository() {
		File repoDir = new File(path + name);
		try {
			repository = FileRepositoryBuilder.create(new File(repoDir, ".git"));
			repository.create();
		} catch (IOException e) {
			System.err.println("SPL creation failed!");
		}
	}

	private void inputProducts() {
		System.out.print("How many product variants you have? ");
		int nbProducts = Integer.parseInt(input.nextLine());
		for (int i = 0; i < nbProducts; i++) {
			addProduct();
		}
	
	}

	private void addProduct() {
		System.out.print("Enter product name: ");
		String productName = input.nextLine();
		System.out.print("Enter product description: ");
		String productDescription = input.nextLine();
		System.out.print("Enter the path where the product is located: ");
		String productPath = input.nextLine();
		Product product = new Product(productName, productDescription, productPath);
		System.out.println("Enter features implemented in product (separated by commas): ");
		String[] productFeatures = input.nextLine().split(",");
		for (int i = 0; i < productFeatures.length; i++) {
			if (features.get(productFeatures[i].trim()) != null) {
				product.getFm().getFeatures().put(features.get(productFeatures[i].trim()).getName(),
						features.get(productFeatures[i].trim()));
				features.get(productFeatures[i].trim()).getProducts().put(productName, product);
			} else {
				Feature feature = new Feature(productFeatures[i].trim());
				features.put(feature.getName(), feature);
				product.getFm().getFeatures().put(feature.getName(), feature);
				feature.getProducts().put(productName, product);
			}
		}
		try {
			if (!repository.getBranch().equals("master")) {
				createBranch(product.getName());
			}
			addProductToGitRepo(product.getPath());
			product.setCommit(commitProduct(product.getDescription()));
			if (repository.getBranch().equals("master")) {
				new Git(repository).branchRename().setNewName(productName).call();
			}
			identifyAssetsAndInstances(repository.getWorkTree(), product);
			products.put(product.getName(), product);
		} catch (IOException e) {
			System.err.println("Failed to add product " + product.getName() + " to git repo");
		} catch (GitAPIException e) {
			System.err.println("Failed to commit product: " + product.getName());
		} catch (NoWorkTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createBranch(String productName) {
		@SuppressWarnings("resource")
		Git git = new Git(repository);
		try {
			git.branchCreate().setName(productName).call();
			git.checkout().setName(productName).call();
		} catch (GitAPIException e) {
			System.err.println("Failed to create branch for " + productName);
		}

	}

	private void addProductToGitRepo(String productPath) throws IOException {
		File splDir = new File(path + name);
		for (File file : splDir.listFiles()) {
			if (!file.getName().equals(".git")) {
				if (file.isDirectory()) {
					FileUtils.deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		FileUtils.copyDirectory(new File(productPath), new File(path + name));
	}

	private RevCommit commitProduct(String productName) throws NoFilepatternException, GitAPIException {
		RevCommit commit;
		@SuppressWarnings("resource")
		Git git = new Git(repository);
		git.add().addFilepattern(".").call();
		commit = git.commit().setAll(true).setMessage("Commit " + productName).call();
		return commit;
	}

	public void printFeatures() throws FileNotFoundException {
		/*
		 * System.out.println("Features: "); for (Feature feature :
		 * features.values()) { System.out.println("- " + feature.getName() +
		 * " FPD: " + feature.getFeaturePropagationDegree() + " FCD: " +
		 * feature.getFeatureCorrelationDegree()); }
		 */

		text.append("Features: \n");
		for (Feature feature : features.values()) {
			text.append("- " + feature.getName() + " FPD: " + feature.getFeaturePropagationDegree() + " FCD: "
					+ feature.getFeatureCorrelationDegree() + "\n");
		}

	}

	public void printProducts() {

		/*
		 * System.out.println("Products"); for (Product product :
		 * products.values()) { System.out.println("- " + product.getName() +
		 * " features: " + product.getFm().getFeatures().size()); }
		 */

		text.append("Products \n");
		for (Product product : products.values()) {
			text.append("- " + product.getName() + " features: " + product.getFm().getFeatures().size() + "\n");
		}

	}

	private void identifyAssetsAndInstances(File directory, Product product)
			throws NoSuchAlgorithmException, IOException {
		for (File file : directory.listFiles()) {
			if (!file.isDirectory()) {
				if (!file.getName().equals(".DS_Store")) {
					if (products.size() == 0
							|| (!assets.containsKey(file.getAbsolutePath().replace(path + name + "/", "")))) {
						Asset asset = new Asset(file.getAbsolutePath().replace(path + name + "/", ""));
						assets.put(asset.getPath(), asset);
						AssetInstance assetInstance = new AssetInstance(asset.getAssetInstances().size() + 1,
								getSHA1(file.getAbsolutePath()), asset);
						asset.getAssetInstances().put(assetInstance.getSha5(), assetInstance);
						asset.getProducts().put(product.getName(), product);
						product.getAssetInstances().put(asset, assetInstance);
						assetInstance.getProducts().add(product);
					} else {
						Asset asset = assets.get(file.getAbsolutePath().replace(path + name + "/", ""));
						String assetInstanceSHA = getSHA1(file.getAbsolutePath());
						if (asset.getAssetInstances().containsKey(assetInstanceSHA)) {
							product.getAssetInstances().put(asset, asset.getAssetInstances().get(assetInstanceSHA));
							asset.getAssetInstances().get(assetInstanceSHA).getProducts().add(product);
						} else {
							AssetInstance assetInstance = new AssetInstance(asset.getAssetInstances().size() + 1,
									assetInstanceSHA, asset);
							asset.getAssetInstances().put(assetInstanceSHA, assetInstance);
							product.getAssetInstances().put(asset, assetInstance);
							assetInstance.getProducts().add(product);
						}
						asset.getProducts().put(product.getName(), product);
					}
				}
			} else if (!file.getName().equals(".git")) {
				identifyAssetsAndInstances(file, product);
			}
		}
	}

	private String getSHA1(String datafile) throws NoSuchAlgorithmException, IOException {

		MessageDigest md = MessageDigest.getInstance("SHA1");
		FileInputStream fis = new FileInputStream(datafile);
		byte[] dataBytes = new byte[1024];
		int nread = 0;
		while ((nread = fis.read(dataBytes)) != -1) {
			md.update(dataBytes, 0, nread);
		}
		byte[] mdbytes = md.digest();

		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		fis.close();
		return sb.toString();
	}

	public void printAssets() {

		/*
		 * System.out.println("Assets: "); for(Asset asset: assets.values()){
		 * System.out.println("- " + asset.getPath() + " AJD: " +
		 * asset.getAssetJunctionDegree() + " ACD: " +
		 * asset.getAssetCorrelationDegree() + " AVR: " +
		 * asset.getAssetVariabilityRatio()); //for(AssetInstance assetInstance:
		 * asset.getAssetInstances().values()){ // System.out.println("\t- " +
		 * assetInstance.getInstanceNo()); //}
		 */

		text.append("Assets: \n");
		for (Asset asset : assets.values()) {
			text.append("- " + asset.getPath() + " AJD: " + asset.getAssetJunctionDegree() + " ACD: "
					+ asset.getAssetCorrelationDegree() + " AVR: " + asset.getAssetVariabilityRatio() + "\n");

		}
	}

	private void constructFeatureToAssetCorrelations() {
		correlations = new ArrayList<FeatureToAssetCorrelation>();
		for (Asset asset : assets.values()) {
			for (Feature feature : features.values()) {
				if (asset.getProducts().values().containsAll(feature.getProducts().values())
						&& asset.getProducts().size() == feature.getProducts().size()) {
					FeatureToAssetCorrelation correlation = new FeatureToAssetCorrelation(feature, asset,
							CorrelationType.EQUIVALENCE, 1);
					correlations.add(correlation);
					asset.getCorrelations().put(feature, correlation);
					correlation.getFeature()
							.setFeaturePropagationDegree(correlation.getFeature().getFeaturePropagationDegree() + 1);
					correlation.getAsset().setAssetJunctionDegree(correlation.getAsset().getAssetJunctionDegree() + 1);
				} else if (asset.getProducts().values().containsAll(feature.getProducts().values())
						&& asset.getProducts().size() > feature.getProducts().size()
						&& isValidImplication(feature, asset)) {
					FeatureToAssetCorrelation correlation = new FeatureToAssetCorrelation(feature, asset,
							CorrelationType.IMPLICATION, 2);
					correlations.add(correlation);
					asset.getCorrelations().put(feature, correlation);
					correlation.getFeature()
							.setFeaturePropagationDegree(correlation.getFeature().getFeaturePropagationDegree() + 1);
					correlation.getAsset().setAssetJunctionDegree(correlation.getAsset().getAssetJunctionDegree() + 1);
				}
			}
		}
	}

	private boolean isValidImplication(Feature feature, Asset asset) {
		boolean isValid = true;
		ArrayList<Product> havingFeature = new ArrayList<Product>();
		ArrayList<Product> notHavingFeature = new ArrayList<Product>();
		for (Product product : products.values()) {
			if (product.getAssetInstances().containsKey(asset)) {
				if (product.getFm().getFeatures().containsKey(feature.getName())) {
					havingFeature.add(product);
					if (foundSameInstance(notHavingFeature, product.getAssetInstances().get(asset))) {
						isValid = false;
						break;
					}
				} else {
					notHavingFeature.add(product);
					if (foundSameInstance(havingFeature, product.getAssetInstances().get(asset))) {
						isValid = false;
						break;
					}
				}
			}

		}
		return isValid;
	}

	private boolean foundSameInstance(ArrayList<Product> products, AssetInstance instanceToFind) {
		boolean found = false;
		find: for (Product product : products) {
			for (AssetInstance assetInstance : product.getAssetInstances().values()) {
				if (assetInstance.equals(instanceToFind)) {
					found = true;
					break find;
				}
			}
		}
		return found;
	}

	public void printFeatureToAssetCorrelations() {

		/*
		 * System.out.println("Correlations:"); for(FeatureToAssetCorrelation
		 * correlation: correlations){
		 * System.out.print(correlation.getFeature().getName());
		 * if(correlation.getCorrelationType().equals(CorrelationType.
		 * EQUIVALENCE)){ System.out.print(" <==> "); }else{
		 * System.out.print(" ==> "); }
		 * System.out.println(correlation.getAsset().getPath() + " " +
		 * correlation.getCorrelationDegree()); }
		 */

		text.append("Correlations: \n");
		for (FeatureToAssetCorrelation correlation : correlations) {
			text.append(correlation.getFeature().getName());
			if (correlation.getCorrelationType().equals(CorrelationType.EQUIVALENCE)) {
				text.append(" <==> ");
			} else {
				text.append(" ==> ");
			}
			text.append(correlation.getAsset().getPath() + " " + correlation.getCorrelationDegree() + "\n");
		}

	}

	private void computeCorrelationIndicators() {

		for (FeatureToAssetCorrelation correlation : correlations) {
			correlation.setCorrelationDegree(1.0
					/ ((correlation.getCorrelationTypeDegree() * correlation.getFeature().getFeaturePropagationDegree()
							* correlation.getAsset().getAssetJunctionDegree())));
			correlation.getFeature().setFeatureCorrelationDegree(
					correlation.getFeature().getFeatureCorrelationDegree() + correlation.getCorrelationDegree());
			correlation.getAsset().setAssetCorrelationDegree(
					correlation.getAsset().getAssetCorrelationDegree() + correlation.getCorrelationDegree());
		}

		instanceCorrelations = new ArrayList<FeatureToAssetInstanceCorrelation>();

		for (Asset asset : assets.values()) {
			for (AssetInstance assetInstance : asset.getAssetInstances().values()) {
				ArrayList<Feature> aiProductsFeatures = new ArrayList<Feature>();
				for (Product product : assetInstance.getProducts()) {
					aiProductsFeatures.addAll(product.getFm().getFeatures().values());
				}
				for (FeatureToAssetCorrelation correlation : correlations) {
					if (correlation.getAsset().equals(asset) && aiProductsFeatures.contains(correlation.getFeature())) {
						assetInstance.setAssetInstanceCorrelationDegree(
								assetInstance.getAssetInstanceCorrelationDegree() + correlation.getCorrelationDegree());
						FeatureToAssetInstanceCorrelation instanceCorrelation = new FeatureToAssetInstanceCorrelation(
								correlation.getFeature(), assetInstance, correlation.getCorrelationDegree());
						instanceCorrelations.add(instanceCorrelation);
						assetInstance.getCorrelations().put(instanceCorrelation.getFeature(), instanceCorrelation);
					}
				}
			}
		}

		for (FeatureToAssetInstanceCorrelation instanceCorrelation : instanceCorrelations) {
			instanceCorrelation.setCorrelationDegree((instanceCorrelation.getFeatureToAssetCorrelationDegree()
					* instanceCorrelation.getAssetInstance().getAssetInstanceCorrelationDegree())
					/ instanceCorrelation.getAssetInstance().getAsset().getAssetCorrelationDegree());
		}

		for (Asset asset : assets.values()) {
			asset.setAssetVariabilityRatio((double) asset.getAssetInstances().size() / asset.getProducts().size());
		}
	}

	public void printAssetInstances() {

		/*
		 * System.out.println("Asset instances: "); for(Asset asset:
		 * assets.values()){ for(AssetInstance assetInstance:
		 * asset.getAssetInstances().values()){
		 * System.out.println(assetInstance.getAsset().getPath() + " (" +
		 * assetInstance.getInstanceNo() + "): AICD " +
		 * assetInstance.getAssetInstanceCorrelationDegree()); } }
		 */

		text.append("Asset instances: \n");
		for (Asset asset : assets.values()) {
			for (AssetInstance assetInstance : asset.getAssetInstances().values()) {
				text.append(assetInstance.getAsset().getPath() + " (" + assetInstance.getInstanceNo() + "): AICD "
						+ assetInstance.getAssetInstanceCorrelationDegree() + "\n");
			}
		}

	}

	public void printFeatureToAssetInstancesCorrelations() {

		text.append("Instance correlations: \n");
		for (FeatureToAssetInstanceCorrelation instanceCorrelation : instanceCorrelations) {
			text.append(instanceCorrelation.getFeature().getName() + " | "
					+ instanceCorrelation.getAssetInstance().getAsset().getPath() + " ("
					+ instanceCorrelation.getAssetInstance().getInstanceNo() + ") | CD: "
					+ instanceCorrelation.getCorrelationDegree() + "\n");
		}

	}

	public void printSPL() throws FileNotFoundException {
		try (PrintWriter out = new PrintWriter(path+name+"correlations")) {
			out.println(text);
		}
	}

	public void createConfiguration() {
		System.out.println("Enter the features you desire to have in your product (separeted by commas): ");
		String[] requiredFeatures = input.nextLine().split(",");
		ArrayList<Feature> existingFeatures = new ArrayList<Feature>();
		ArrayList<String> newFeatures = new ArrayList<String>();
		for (int i = 0; i < requiredFeatures.length; i++) {
			Feature feature = features.get(requiredFeatures[i].trim());
			if (feature != null) {
				existingFeatures.add(feature);
			} else {
				newFeatures.add(requiredFeatures[i].trim());
			}
		}
		Product realizingProduct = getRealizingProduct(existingFeatures);
		if (realizingProduct != null) {
			System.out.println("Product " + realizingProduct.getName() + " realizes the features:");
			for (Feature feature : realizingProduct.getFm().getFeatures().values()) {
				System.out.println("- " + feature.getName());
			}
			if (newFeatures.size() > 0) {
				System.out.println("Features to add:");
				for (String feature : newFeatures) {
					System.out.println("- " + feature);
				}
			}
		} else {
			System.out.println("No product realizes the required features");
			ArrayList<Combination> combinations = getProductsCombinations(existingFeatures);
			printCombinations(combinations);
			for (Combination combination : combinations) {
				HashMap<Asset, HashMap<AssetInstance, ArrayList<Product>>> assetInstances = new HashMap<Asset, HashMap<AssetInstance, ArrayList<Product>>>();
				for (Product product : combination.getProducts()) {
					for (AssetInstance assetInstance : product.getAssetInstances().values()) {
						if (isRequiredAssetInstance(assetInstance, existingFeatures)) {
							if (assetInstances.get(assetInstance.getAsset()) == null) {
								HashMap<AssetInstance, ArrayList<Product>> newAssetInstance = new HashMap<AssetInstance, ArrayList<Product>>();
								ArrayList<Product> newProduct = new ArrayList<Product>();
								newProduct.add(product);
								newAssetInstance.put(assetInstance, newProduct);
								assetInstances.put(assetInstance.getAsset(), newAssetInstance);
							} else {
								if (!assetInstances.get(assetInstance.getAsset()).keySet().contains(assetInstance)) {
									ArrayList<Product> newProduct = new ArrayList<Product>();
									newProduct.add(product);
									assetInstances.get(assetInstance.getAsset()).put(assetInstance, newProduct);
								} else {
									assetInstances.get(assetInstance.getAsset()).get(assetInstance).add(product);
								}
							}
						}
					}
				}

				for (Asset asset : assetInstances.keySet()) {

					ArrayList<Feature> assetRequiredFeatures = new ArrayList<Feature>();
					for (Feature feature : asset.getCorrelations().keySet()) {
						if (existingFeatures.contains(feature)) {
							assetRequiredFeatures.add(feature);
						}
					}

					AssetInstance realizingAssetInstance = null;

					for (AssetInstance assetInstance : assetInstances.get(asset).keySet()) {
						if (assetInstance.getCorrelations().keySet().containsAll(assetRequiredFeatures)
								&& assetInstance.getCorrelations().keySet().size() == assetRequiredFeatures.size()) {
							realizingAssetInstance = assetInstance;
						}
					}
					if (realizingAssetInstance != null) {
						Action action = new Action(ActionType.CloneAndRetain, realizingAssetInstance);
						action.setProducts(assetInstances.get(asset).get(realizingAssetInstance));
						Operation operation = new Operation();
						operation.getActions().add(action);
						setOperationDescription(operation);
						combination.getOperations().put(asset, new ArrayList<Operation>(Arrays.asList(operation)));
					} else {
						HashMap<Operation, ArrayList<Feature>> undoneOperations = new HashMap<Operation, ArrayList<Feature>>();
						for (AssetInstance assetInstance : assetInstances.get(asset).keySet()) {
							ArrayList<Feature> featuresToRetain = new ArrayList<Feature>();
							ArrayList<Feature> featuresToRemove = new ArrayList<Feature>();
							ArrayList<Feature> remainingFeatures = new ArrayList<Feature>();
							remainingFeatures.addAll(assetRequiredFeatures);
							for (Feature feature : assetInstance.getCorrelations().keySet()) {
								if (assetRequiredFeatures.contains(feature)) {
									featuresToRetain.add(feature);
								} else {
									featuresToRemove.add(feature);
								}
							}
							remainingFeatures.removeAll(featuresToRetain);
							Operation operation = new Operation();
							if (featuresToRemove.size() == 0) {
								Action action = new Action(ActionType.CloneAndRetain, assetInstance);
								action.setFeaturesToRetain(featuresToRetain);
								action.setProducts(assetInstances.get(asset).get(assetInstance));
								operation.getActions().add(action);
							} else {
								Action action = new Action(ActionType.CloneAndRemove, assetInstance);
								action.setFeaturesToRetain(featuresToRetain);
								action.setFeaturesToRemove(featuresToRemove);
								action.setProducts(assetInstances.get(asset).get(assetInstance));
								double cost = 0.0;
								for (Feature feature : featuresToRemove) {
									cost += assetInstance.getCorrelations().get(feature).getCorrelationDegree();
								}
								action.setCost(cost);
								operation.getActions().add(action);
								operation.setCost(cost);
							}
							if (remainingFeatures.size() > 0) {
								undoneOperations.put(operation, remainingFeatures);
							} else {
								setOperationDescription(operation);
								combination.getOperations().put(asset,
										new ArrayList<Operation>(Arrays.asList(operation)));
							}
						}
						while (undoneOperations.size() > 0) {
							HashMap<Operation, ArrayList<Feature>> undoneOperationsToAdd = new HashMap<Operation, ArrayList<Feature>>();
							ArrayList<Operation> doneOperations = new ArrayList<Operation>();
							for (Operation operation : undoneOperations.keySet()) {
								for (AssetInstance assetInstance : assetInstances.get(asset).keySet()) {
									if (assetInstance.getCorrelations().keySet()
											.contains(undoneOperations.get(operation).get(0))) {
										ArrayList<Feature> remainingFeatures = new ArrayList<Feature>();
										remainingFeatures.addAll(undoneOperations.get(operation));
										Action action = new Action(ActionType.ExtractAndAdd, assetInstance);
										ArrayList<Feature> featuresToRemove = new ArrayList<Feature>();
										double cost = 0.0;
										for (Feature feature : remainingFeatures) {
											if (assetInstance.getCorrelations().containsKey(feature)) {
												cost += assetInstance.getCorrelations().get(feature)
														.getFeatureToAssetCorrelationDegree();
												featuresToRemove.add(feature);
											}
										}
										remainingFeatures.removeAll(featuresToRemove);
										action.setFeaturesToRemove(featuresToRemove);
										action.setProducts(assetInstances.get(asset).get(assetInstance));
										action.setCost(cost);
										Operation newOperation = new Operation();
										newOperation.getActions().addAll(operation.getActions());
										newOperation.getActions().add(action);
										newOperation.setCost(operation.getCost() + cost);
										if(remainingFeatures.size()>0){
											undoneOperationsToAdd.put(newOperation, undoneOperations.get(operation));
										}else{
											setOperationDescription(newOperation);
											doneOperations.add(newOperation);
										}
									}
								}
							}
							undoneOperations.clear();
							undoneOperations.putAll(undoneOperationsToAdd);
							combination.getOperations().get(asset).addAll(doneOperations);
							
						}
					}

					
					double operationCost = Double.POSITIVE_INFINITY;
					for(Operation operation: combination.getOperations().get(asset)){
						operation.setCost(operation.getCost()*asset.getAssetVariabilityRatio());
						if(operation.getCost()<operationCost){
							operationCost = operation.getCost();
						}
					}
					combination.setCost(combination.getCost()+operationCost);
					
				}
				System.out.println("\n"+combination.getName() + " cost = " + combination.getCost()+"\n");
				for(Asset asset: combination.getOperations().keySet()){
					System.out.println(asset.getPath()+"\n");
					for(int i=0; i<combination.getOperations().get(asset).size(); i++){
						Operation operation = combination.getOperations().get(asset).get(i);
						System.out.println("Operation " + (i+1) + ", cost (" + operation.getCost() + ") \n" + operation.getDescription());
					}
					
				}
				
			}
		}
	}

	public Product getRealizingProduct(ArrayList<Feature> existingFeatures) {
		Product realizingProduct = null;
		for (Product product : products.values()) {
			if (product.getFm().getFeatures().values().containsAll(existingFeatures)
					&& product.getFm().getFeatures().size() == existingFeatures.size()) {
				realizingProduct = product;
				break;
			}
		}
		return realizingProduct;
	}

	public ArrayList<Combination> getProductsCombinations(ArrayList<Feature> existingFeatures) {
		ArrayList<Combination> combinations = new ArrayList<Combination>();
		ArrayList<Product> containingProducts = new ArrayList<Product>();
		for (Feature feature : existingFeatures) {
			containingProducts.addAll(feature.getProducts().values());
		}
		Set<Product> productsSet = new HashSet<Product>(containingProducts);
		Set<Set<Product>> combinationsSet = Sets.powerSet(productsSet);
		Iterator<Set<Product>> combinationsIterator = combinationsSet.iterator();
		while (combinationsIterator.hasNext()) {
			ArrayList<Product> productsList = new ArrayList<Product>(combinationsIterator.next());
			if (productsList.size() > 0) {
				ArrayList<Feature> featuresToRetain = new ArrayList<Feature>();

				for (Product product : productsList) {
					for (Feature feature : product.getFm().getFeatures().values()) {
						if (existingFeatures.contains(feature)) {
							 featuresToRetain.add(feature);
						
						}
					}
				}
				if (featuresToRetain.containsAll(existingFeatures)) {

					Combination combination = new Combination(productsList, "c" + (combinations.size() + 1));
					combinations.add(combination);
				}
			}
		}
		return combinations;
	}

	public void printCombinations(ArrayList<Combination> combinations) {
		for (Combination combination : combinations) {
			System.out.print(combination.getName() + ": | ");
			for (Product product : combination.getProducts()) {
				System.out.print(product.getName() + " | ");
			}
			System.out.println();
		}
	}

	

	private boolean isRequiredAssetInstance(AssetInstance assetInstance, ArrayList<Feature> requiredFeatures) {
		for (Feature feature : requiredFeatures) {
			if (assetInstance.getCorrelations().keySet().contains(feature)) {
				return true;
			}
		}
		return false;
	}

	private void setOperationDescription(Operation operation){
		String description = "";
		for(int i=0; i< operation.getActions().size(); i++){
			Action action = operation.getActions().get(i);
			description += (i+1) + "- ";
			switch(action.getType()){
				case CloneAndRetain:
					description += "Clone and retain " + action.getAssetInstance().getAsset().getPath() + " (" 
							+ action.getAssetInstance().getInstanceNo() + ") existing in | " ;
					for(Product product: action.getProducts()){
						description += product.getName() + " |";
					}
					description += "\n";
					break;
				case CloneAndRemove:
					description += "Clone " + action.getAssetInstance().getAsset().getPath() + " (" 
							+ action.getAssetInstance().getInstanceNo() + ") existing in | " ;
					for(Product product: action.getProducts()){
						description += product.getName() + " |";
					}
					description += " and remove from it implementation fragments related to feature(s) | ";
					for(Feature feature : action.getFeaturesToRemove()){
						description += feature.getName() + " |";
					}
					description += "\n";
					break;
				case ExtractAndAdd:
					description += "Extract from " + action.getAssetInstance().getAsset().getPath() + " (" 
							+ action.getAssetInstance().getInstanceNo() + ") existing in | " ;
					for(Product product: action.getProducts()){
						description += product.getName() + " |";
					}
					description += " implementation fragments related to feature(s) | ";
					for(Feature feature: action.getFeaturesToRemove()){
						description += feature.getName() + " |";
					}
					description += " and add it to the cloned instance\n";
			}
		}
		operation.setDescription(description);
	}
	
}
