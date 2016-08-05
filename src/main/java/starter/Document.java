package starter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

class Node{

	public Heading heading;
	public List<Node> children;
	public boolean recent;
	
	public Node() {
		super();
		this.children = new ArrayList<Node>();
	}

	public Heading getHeading() {
		return heading;
	}

	public void setHeading(Heading heading) {
		this.heading = heading;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void addChildren(Node child) {
		this.children.add(child);
	}
	
	public void setRecent(boolean recent){
		this.recent = recent;
	}
	
	public boolean isRecent(){
		return this.recent;
	}

	@Override
	public String toString() {
		return "Node [heading=" + heading + ", children=" + children
				+ ", recent=" + recent + "]";
	}

	


}

class Heading {

	public String text;
	public Integer weight;
	    
	public Heading(String type, String value) {
		super();
		this.text = value;
		this.weight = IndexUtility.getWeight(type);
	}
	
	@Override
	public String toString() {
		return "[ " + this.weight + " - " + this.text + " ]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Heading other = (Heading) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}

	
}

class DocumentParserEngine {

	public String filePath;
	public Node node;

	public DocumentParserEngine(String filePath) {
		this.filePath = filePath;
		this.node = new Node();
	}

	public void generateParsedDocument() throws IOException {

		Node currentNode;
		List<String> lines = this.readDocument();

		for (String line : lines) {

			if (line.isEmpty() || !line.toUpperCase().startsWith("HEADING"))
				continue;

			String[] lineSplit = line.split("\\t");
			Heading displayObject = new Heading(lineSplit[0].trim(),lineSplit[1].trim());
			currentNode = new Node();
			currentNode.heading = displayObject;
			currentNode.setRecent(true);
			
			//if root node doesn't have child, add first node as child
			if(this.node.getChildren().isEmpty()) {
				this.node.addChildren(currentNode);
			} else {
				//find out where to add next node 
				Node latestNode = this.getLatestNode(this.node);
				
				//latestnode is null as there is a change in weight, find node where its weight is one weight low from latest node
				if(latestNode==null){
					latestNode = this.findNode(this.node,currentNode);
				}
				
				latestNode.setRecent(false);
				if(currentNode.heading.weight > latestNode.heading.weight){
					this.addNode(latestNode,currentNode);
				} else if (currentNode.heading.weight == latestNode.heading.weight) {
					//find parent of latest node and add current node into it
					Node parentNode = this.getParentNode(latestNode,this.node);
					parentNode.addChildren(currentNode);
					currentNode.setRecent(false);
				} 
			}
		}
	}

	private Node getParentNode(Node latestNode,Node parentNode) {
		for(Node nodeChild: parentNode.getChildren()){
			if(nodeChild.heading.equals(latestNode.heading))
				return parentNode;
			else
				return getParentNode(latestNode,nodeChild);
		}
		return null;
	}
	
	private Node findNode(Node master,Node currentNode) {
			
		for(Node nodeChild: master.getChildren()){
			if(nodeChild.heading.weight==currentNode.heading.weight)
				return master;
			else
				return findNode(nodeChild,currentNode);
		}
		return null;
	}

	private Node getLatestNode(Node node) {
		
		for(Node nodeChild: node.getChildren()){
			if(nodeChild.isRecent())
				return nodeChild;
			else
				return getLatestNode(nodeChild);
		}
		return null;
	}

	private boolean addNode(Node parent, Node childNode) {
		
		//find out where to add next node
		int totalChild = parent.getChildren().size();
		if(totalChild == 0){
			parent.addChildren(childNode);
			return true;
		}
		Node latestNode = parent.getChildren().get(totalChild-1);
		int latestNodeWeight = childNode.heading.weight;
		
		if(parent.heading.weight > latestNodeWeight){
			this.addNode(latestNode,childNode);
		} else{ 
			parent.addChildren(latestNode);
		}
		return true;
	}

	public void getGeneratedTree() {
		System.out.print(new Gson().toJson(this.node));
	}

	public List<String> readDocument() throws IOException {
		return Files.readAllLines(Paths.get(this.filePath), Charset.defaultCharset());
	}

}

class IndexUtility {
	public static Integer getWeight(String type) {
		if (!type.isEmpty() && type.toUpperCase().contains("HEADING"))
			return Integer.parseInt(type.toUpperCase().replaceAll("HEADING", "").trim());
		
		return 1;
	}
}

public class Document {

	public static void main(String[] args) {
		try {
			
			String filePath = "//home//chiragparmar//workspace//Predictive//src//main//java//starter//document//document.txt";
			DocumentParserEngine docEngine = new DocumentParserEngine(filePath);
			docEngine.generateParsedDocument();
			docEngine.getGeneratedTree();
			
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}

}