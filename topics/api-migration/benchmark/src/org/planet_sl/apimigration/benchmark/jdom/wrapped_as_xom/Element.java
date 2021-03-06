package org.planet_sl.apimigration.benchmark.jdom.wrapped_as_xom;

import static org.planet_sl.apimigration.benchmark.jdom.wrapped_as_xom.Utils.content2node;
import static org.planet_sl.apimigration.benchmark.jdom.wrapped_as_xom.Utils.node2content;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.planet_sl.apimigration.benchmark.anno.Issue;
import org.planet_sl.apimigration.benchmark.anno.MapsTo;
import org.planet_sl.apimigration.benchmark.anno.Progress;
import org.planet_sl.apimigration.benchmark.anno.Solution;
import org.planet_sl.apimigration.benchmark.anno.Unresolved;
import org.planet_sl.apimigration.benchmark.anno.Wrapping;
import org.planet_sl.apimigration.benchmark.anno.Progress.Status;
import org.planet_sl.apimigration.benchmark.anno.Solution.Strategy;
import org.planet_sl.apimigration.benchmark.anno.Unresolved.XML;

@SuppressWarnings("unchecked")
@MapsTo("org.jdom.Element")
public class Element extends ParentNode {
	@Wrapping
	org.jdom.Element element;

	@Wrapping
	Element(org.jdom.Element element) {
		this.element = element;
	}

	// / XOM api starts below

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#clone()")
	public Element(Element element) {
		this((org.jdom.Element) element.element.clone());
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@Issue.Pre(value = "XOM allows prefixed names where jdom does not", resolved = true)
	@Issue.Throws(value = "XOM throws NullPointerException where considers null to be an IllegalName", resolved = true)
	@MapsTo("org.jdom.Element(String)")
	public Element(String name) {
		if (name == null) {
			throw new NullPointerException("null name");
		}
		String prefix = "";
        String localName = name;
        int colon = name.indexOf(':');
        if (colon > 0) {
            prefix = name.substring(0, colon);   
            localName = name.substring(colon + 1);   
        }
		try {
			element = new org.jdom.Element(localName, prefix, "");
		} 
		catch (org.jdom.IllegalNameException e) {
			throw new IllegalNameException(e, name);
		}
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@Issue.Pre(value = "XOM allows prefixed names where jdom does not", resolved = true)
	@Issue.Throws(value = "XOM throws NullPointerException where considers null to be an IllegalName", resolved = true)
	@MapsTo("org.jdom.Element(String,String,String)")
	public Element(String name, String uri) {
		if (name == null) {
			throw new NullPointerException("null name");
		}
		int index = name.indexOf(":");
		String prefix = "";
		String localName = name;
		if (index != -1) {
			prefix = name.substring(0, index);
			localName = name.substring(index + 1, name.length());
		}
		if (prefix.equals("xmlns")) {
			throw new NamespaceConflictException(prefix);
		}
		if (prefix.equals("xml") && !uri.equals(org.jdom.Namespace.XML_NAMESPACE.getURI())) {
			throw new NamespaceConflictException(prefix);
		}
		if (!prefix.equals("xml") && uri.equals(org.jdom.Namespace.XML_NAMESPACE.getURI())) {
			throw new NamespaceConflictException(prefix);
		}
		String error = org.jdom.Verifier.checkNamespaceURI(uri);
		if (error != null) {
			throw new MalformedURIException(error);
		}
		error = org.jdom.Verifier.checkURI(uri);
		if (error != null) {
			throw new MalformedURIException(error);
		}
		try {
			element = new org.jdom.Element(localName, prefix, uri);
		} catch (org.jdom.IllegalNameException e) {
			throw new IllegalNameException(e, name);
		}
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@Issue.Throws(value = "XOM throws CycleException when JDOM throws IllegalAddException; " +
			"MultipleParentException instead of IllegalAddException too", resolved = true)
	@Override
	@MapsTo("org.jdom.Element#addContent(org.jdom.Content)")
	public void appendChild(Node child) {
		if (child == this) {
			throw new CycleException("cannot add child to itself");
		}
		// This is actually part of wrapping.
		if (child instanceof Document) {
			throw new IllegalAddException("cannot add document to elements");
		}
		org.jdom.Parent parent = element; 
		while (parent != null) {
			if (parent == node2content(child)) {
				throw new CycleException("cannot add child to itself");
			}
			parent = parent.getParent(); 
		}
		if (child.getParent() != null) {
			throw new MultipleParentException("child already as has parent");
		}
		try {
			element.addContent(node2content(child));
		} catch (org.jdom.IllegalAddException e) {
			throw new IllegalAddException(e.getMessage(), e);
		}
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@Override
	@MapsTo("org.jdom.Element#getContent(int)")
	public Node getChild(int position) {
		return content2node(element.getContent(position));
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@Override
	@MapsTo("org.jdom.getContentSize()")
	public int getChildCount() {
		return element.getContentSize();
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@Override
	@MapsTo("org.jdom.Element#indexOf(org.jdom.Content)")
	public int indexOf(Node child) {
		return element.indexOf(node2content(child));
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@Issue.Throws(value = "XOM throws NullPointerException where considers null to be an IllegalName", resolved = true)
	@Override
	@MapsTo("org.jdom.Element(int,org.jdom.Content)")
	public void insertChild(Node child, int position) {
		if (child == null) {
			throw new NullPointerException("inserting null child");
		}
		if (child instanceof Document) {
			throw new IllegalAddException("documents cannot be childs of elements");
		}
		if (child.getParent() != null) {
			throw new MultipleParentException("child has a parent");
		}
		element.addContent(position, node2content(child));
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@Override
	@MapsTo("org.jdom.Element#removeContent(int)")
	public Node removeChild(int position) {
		return content2node(element.removeContent(position));
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@Issue.Pre(value = "child must be a child of this element; jdom silently continues if not so",
		resolved = true)
	@Override
	@MapsTo("org.jdom.Element@removeContent(org.jdom.Content)")
	public Node removeChild(Node child) {
		int index = element.indexOf(node2content(child));
		if (index == -1) {
			throw new NoSuchChildException(
					"child is not a child of this element");
		}
		element.removeContent(node2content(child));
		return child;
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.MACRO, comment = "")
	@Override
	@MapsTo("")
	public void replaceChild(Node oldChild, Node newChild) {
		if (oldChild == null) {
			throw new NullPointerException("oldChild == null");
		}
		if (newChild == null) {
			throw new NullPointerException("newChild == null");
		}

		if (newChild instanceof Document) {
			throw new IllegalAddException("cannot add documents to elements");
		}

		if (newChild instanceof DocType) {
			throw new IllegalAddException("cannot add doctypes to elements");
		}

		if (newChild instanceof Attribute) {
			throw new IllegalAddException("cannot add attributes to elements");
		}

		int index = element.indexOf(node2content(oldChild));
		if (index == -1) {
			throw new NoSuchChildException(
					"oldChild is not a child of this element");
		}
		try {
			element.setContent(index, node2content(newChild));
		} catch (org.jdom.IllegalAddException e) {
			throw new MultipleParentException(e);
		}

	}

	@Progress(value = Status.NEEDSWORK, comment = "")
	@Solution(value = Strategy.CLONE, comment = "")
	@Issue.Pre("xom agressively checks uri for well-formedness and throws accordingly")
	@Override
	@Unresolved(XML.BaseURI)
	@MapsTo("")
	public void setBaseURI(String uri) {
		/*
		 * Sets the URI against which relative URIs in this node will be
		 * resolved. Generally, it's only necessary to set this property if it's
		 * different from a node's parent's base URI, as it may be in a document
		 * assembled from multiple entities or by XInclude.
		 * 
		 * Relative URIs are not allowed here. Base URIs must be absolute.
		 * However, the base URI may be set to null or the empty string to
		 * indicate that the node has no explicit base URI. In this case, it
		 * inherits the base URI of its parent node, if any.
		 * 
		 * URIs with fragment identifiers are also not allowed. The value passed
		 * to this method must be a pure URI, not a URI reference.
		 * 
		 * You can also add an xml:base attribute to an element in the same way
		 * you'd add any other namespaced attribute to an element. If an
		 * element's base URI conflicts with its xml:base attribute, then the
		 * value found in the xml:base attribute is used.
		 * 
		 * If the base URI is null or the empty string and there is no xml:base
		 * attribute, then the base URI is determined by the nearest ancestor
		 * node which does have a base URI. Moving such a node from one location
		 * to another can change its base URI.
		 * 
		 * Parameters: URI - the new base URI for this node Throws:
		 * MalformedURIException - if URI is not a legal RFC 3986 absolute URI
		 */

		if (uri == null || uri.equals("")) {
			element.removeAttribute("base", org.jdom.Namespace.XML_NAMESPACE);
			return;
		}
		String error = org.jdom.Verifier.checkURI(uri);
		if (error != null) {
			throw new MalformedURIException(error);
		}
		try {
			URI uriObject = new URI(uri);
			if (uriObject.getFragment() != null) {
				throw new MalformedURIException(
						"no fragments allows in base URIs");
			}
			if (!uriObject.isAbsolute()) {
				throw new MalformedURIException("base URIs should be absolute.");
			}
			try {
				element.setAttribute("base", uriObject.toASCIIString(),
						org.jdom.Namespace.XML_NAMESPACE);
			} catch (org.jdom.IllegalDataException e) {
				throw new MalformedURIException("illegal data in uri");
			}
		} catch (URISyntaxException e) {
			throw new MalformedURIException(e, uri);
		}

	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@Override
	@MapsTo("org.jdom.Element#clone()")
	public Node copy() {
		return new Element((org.jdom.Element) element.clone());
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@Issue.Pre(value = "in XOM this element must not be a root of a document", resolved = true)
	@Issue.Invariant(value = "root must remain attached", resolved = true)
	@Override
	@MapsTo("org.jdom.Element#detach()")
	public void detach() {
		if (element.getDocument() != null) {
			if (element.getDocument().getRootElement() == element) {
				throw new WellformednessException("detached root");
			}
		}
		element.detach();
	}

	@Progress(value = Status.NEEDSWORK, comment = "")
	@Solution(value = Strategy.CLONE, comment = "")
	@Issue.Post("in XOM the result is absolutized and/or converted from IRI to URI")
	@Override
	@Unresolved(XML.BaseURI)
	@MapsTo("")
	public String getBaseURI() {
		/*
		 * The base URI of an element is determined as follows:
		 * 
		 * If the element has an xml:base attribute, then the value of that
		 * attribute is converted from an IRI to a URI, absolutized if possible,
		 * and returned. Otherwise, if any ancestor element of the element
		 * loaded from the same entity has an xml:base attribute, then the value
		 * of that attribute from the nearest such ancestor is converted from an
		 * IRI to a URI, absolutized if possible, and returned. xml:base
		 * attributes from other entities are not considered. Otherwise, if
		 * setBaseURI() has been invoked on this element, then the URI most
		 * recently passed to that method is absolutized if possible and
		 * returned. Otherwise, if the element comes from an externally parsed
		 * entity or the document entity, and the original base URI has not been
		 * changed by invoking setBaseURI(), then the URI of that entity is
		 * returned. Otherwise, (the element was created by a constructor rather
		 * then being parsed from an existing document), the base URI of the
		 * nearest ancestor that does have a base URI is returned. If no
		 * ancestors have a base URI, then the empty string is returned.
		 * Absolutization takes place as specified by the XML Base
		 * specification. However, it is not always possible to absolutize a
		 * relative URI, in which case the empty string will be returned.
		 * 
		 * Returns: the base URI of this node
		 */
		org.jdom.Attribute base = element.getAttribute("base",
				org.jdom.Namespace.XML_NAMESPACE);
		if (base != null) {
			if (base.getValue().equals("")) {
				String uri = element.getDocument().getBaseURI();
				return uri == null ? "" : uri;
			}
			return base.getValue();
		}
		if (getParent() != null) {
			return getParent().getBaseURI();
		}
		return "";
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@Override
	@MapsTo("org.jdom.Element#getDocument()")
	public Document getDocument() {
		if (element.getDocument() == null) {
			return null;
		}
		return new Document(element.getDocument());
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@Override
	@MapsTo("org.jdom.Element#getParent()")
	public ParentNode getParent() {
		org.jdom.Parent parent = element.getParent();
		if (parent == null) {
			return (ParentNode) null;
		}
		if (parent instanceof org.jdom.Element) {
			return new Element((org.jdom.Element) parent);
		}
		if (parent instanceof org.jdom.Document) {
			return new Document((org.jdom.Document) parent);
		}
		throw new AssertionError("invalid parent for element");
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@Override
	@MapsTo("org.jdom.Element#getValue()")
	public String getValue() {
		return element.getValue();
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@Override
	@MapsTo("org.jdom.xpath.XPath#selectNodes(Object)")
	public Nodes query(String query, XPathContext namespaces) {
		try {
			org.jdom.xpath.XPath xpath = org.jdom.xpath.XPath
					.newInstance(query);
			for (Object o : namespaces.namespaces) {
				xpath.addNamespace((org.jdom.Namespace) o);
			}
			List list = xpath.selectNodes(this.element);
			return new Nodes(list);
		} catch (org.jdom.JDOMException e) {
			throw new XPathException(e.getMessage(), e.getCause());
		}
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@Override
	@MapsTo("org.jdom.xpath.XPath#selectNodes(Object)")
	public Nodes query(String query) {
		try {
			org.jdom.xpath.XPath xpath = org.jdom.xpath.XPath
					.newInstance(query);
			List list = xpath.selectNodes(this.element);
			return new Nodes(list);
		} catch (org.jdom.JDOMException e) {
			throw new XPathException(e.getMessage(), e.getCause());
		}
	}

	@Progress(value = Status.DONTCARE, comment = "is debugging aid")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@Issue.Post("the resulting string may be slightly different")
	@Override
	@Unresolved(XML.Serialization)
	@MapsTo("org.jdom.output.XMLOutputter#outputString(Element)")
	public String toXML() {
		return new org.jdom.output.XMLOutputter().outputString(element);
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@Issue.Throws(value = "XOM throws MultipleParentException if argument is parented whereas JDOM throws IllegalAddException", resolved = true)
	@MapsTo("org.jdom.Element#setAttribute()")
	public void addAttribute(Attribute attribute) {
		if (attribute.attribute.getParent() != null) {
			throw new MultipleParentException("has o parent already");
		}
		try {
			element.setAttribute(((Attribute) attribute).attribute);
			if (!attribute.attribute.getNamespaceURI().equals("")) {
				if (!attribute.attribute.getNamespaceURI().equals(org.jdom.Namespace.XML_NAMESPACE.getURI())) {
					element.addNamespaceDeclaration(org.jdom.Namespace.getNamespace(
						attribute.attribute.getNamespacePrefix(), 
						attribute.attribute.getNamespaceURI()));
				}
			}
		} catch (org.jdom.IllegalAddException e) {
			throw new NamespaceConflictException(e);
		}
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@Issue.Throws(value = "XOM throws NamespaceConflictException vs. IllegalAddException in jdom", resolved = true)
	@MapsTo("org.jdom.Element#addNamespaceDeclaration(org.jdom.Namespace)")
	public void addNamespaceDeclaration(String prefix, String uri) {
		if (prefix.equals("xml") && !uri.equals(org.jdom.Namespace.XML_NAMESPACE.getURI())) {
				throw new NamespaceConflictException(prefix);
		}
		try {
			element.addNamespaceDeclaration(org.jdom.Namespace.getNamespace(
					prefix, uri));
		} catch (org.jdom.IllegalNameException e) {
			throw new IllegalNameException(e, uri);
		} catch (org.jdom.IllegalAddException e) {
			throw new NamespaceConflictException(e, element.getNamespaceURI(), uri);
		}
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#addContent(String)")
	public void appendChild(String text) {
		element.addContent(text);
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#getAttribute(String)")
	public Attribute getAttribute(String name) {
		//org.jdom.Attribute attr = element.getAttribute(name, org.jdom.Namespace.NO_NAMESPACE);
		org.jdom.Attribute attr = element.getAttribute(name);
		if (attr == null) {
			return null;
		}
		return new Attribute(attr);
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@Issue.Throws(value = "XOM returns null where jdom throws on illegal attr name", resolved = true)
	@MapsTo("org.jdom.Element#getAttribute(String,org.jdom.Namespace)")
	public Attribute getAttribute(String localName, String namespaceURI) {
		org.jdom.Attribute attr;
		try {
			attr = element.getAttribute(localName, org.jdom.Namespace
					.getNamespace(namespaceURI));
		} catch (org.jdom.IllegalNameException e) {
			return null; // so here XOM returns null???
		}
		if (attr == null) {
			return null;
		}
		return new Attribute(attr);
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.MACRO, comment = "")
	@MapsTo("")
	public Attribute getAttribute(int index) {
		return new Attribute((org.jdom.Attribute) element.getAttributes().get(
				index));
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.MACRO, comment = "")
	@MapsTo("")
	public int getAttributeCount() {
		return element.getAttributes().size();
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#getAttributeValue(String)")
	public String getAttributeValue(String name) {
		org.jdom.Attribute attr = element.getAttribute(name);
		if (attr == null) {
			return null;
		}
		return attr.getValue();
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@Issue.Throws(value = "XOM returns null where jdom throws on illegal attr name", resolved = true)
	@MapsTo("org.jdom.Element#getAttributeValue(String,org.jdom.Namespace")
	public String getAttributeValue(String localName, String namespaceURI) {
		org.jdom.Attribute attr;
		try {
			if (namespaceURI.equals(org.jdom.Namespace.XML_NAMESPACE.getURI())) {
				attr = element.getAttribute(localName, org.jdom.Namespace.XML_NAMESPACE);
			}
			else {
				attr = element.getAttribute(localName, org.jdom.Namespace
						.getNamespace(namespaceURI));
			}
		} catch (org.jdom.IllegalNameException e) {
			System.err.println("EROR" + e.getMessage());
			return null;
		}
		if (attr == null) {
			return null;
		}
		return attr.getValue();
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#getChildren(String)")
	public Elements getChildElements(String name) {
		return new Elements(element.getChildren(name));
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#getChildren(String,org.jdom.Namespace)")
	public Elements getChildElements(String localName, String namespaceURI) {
		if (localName != null && localName.equals("")) {
			localName = null;
		}
		return new Elements(element.getChildren(localName, org.jdom.Namespace
				.getNamespace(namespaceURI)));
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#getChildren()")
	public Elements getChildElements() {
		return new Elements(element.getChildren());
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.MACRO, comment = "")
	@MapsTo("")
	public Element getFirstChildElement(String name) {
		List list = element.getChildren(name);
		if (list.size() > 0) {
			return new Element((org.jdom.Element) list.get(0));
		}
		return null;
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.MACRO, comment = "")
	@MapsTo("")
	public Element getFirstChildElement(String localName, String namespaceURI) {
		List list = element.getChildren(localName, org.jdom.Namespace
				.getNamespace(namespaceURI));
		if (list.size() > 0) {
			return new Element((org.jdom.Element) list.get(0));
		}
		return null;
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#getName()")
	public String getLocalName() {
		return element.getName();
	}

	@Progress(value = Status.NEEDSWORK, comment = "")
	@Solution(value = Strategy.MACRO, comment = "")
	@Issue.Post("TODO: check if result differs because of \"additional\"")
	//("Should also check for xml:... attributes")
	@MapsTo("")
	@Unresolved(XML.Namespacing)
	public int getNamespaceDeclarationCount() {
		int count = element.getAdditionalNamespaces().size();
//		if (element.getNamespace() != null) {
//			count++;
//		}
		return count;
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#getNamespacePrefix")
	public String getNamespacePrefix() {
		return element.getNamespacePrefix();
	}

	@Progress(value = Status.GIVENUP, comment = "")
	@Solution(value = Strategy.MACRO, comment = "")
	@Issue.Post("result is undefined since XOM includes the element's own namespace")
	@MapsTo("")
	@Unresolved(XML.Namespacing)
	public String getNamespacePrefix(int index) {
		// TODO: this is probably an API mismatch
		// A leaky abstraction.
		// XOM includes the namespace of the element itself (where JDOM does not
		// for getAdditionalNamespaces)
		// yet the order XOM uses is arbitrary.
		if (index >= element.getAdditionalNamespaces().size()) {
			throw new IndexOutOfBoundsException("index larger than size - 1");
		}
		if (index == 0) {
			return element.getNamespace().getPrefix();
		}
		                
		return ((org.jdom.Namespace) element.getAdditionalNamespaces().get(
				index - 1)).getPrefix();
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#getNamespaceURI()")
	public String getNamespaceURI() {
		return element.getNamespaceURI();
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.MACRO, comment = "")
	@MapsTo("")
	public String getNamespaceURI(String prefix) {
		if (element.getNamespace(prefix) != null) {
			return element.getNamespace(prefix).getURI();
		}
		for (Object o: element.getAdditionalNamespaces()) {
			org.jdom.Namespace ns = (org.jdom.Namespace)o;
			if (ns.getPrefix().equals(prefix)) {
				return ns.getURI();
			}
		}
		// ????
		return "";
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#getQualifiedName()")
	public String getQualifiedName() {
		return element.getQualifiedName();
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#addContent(int,org.jdom.Text)")
	public void insertChild(String text, int position) {
		if (text == null) {
			throw new NullPointerException("inserting null strinng");
		}
		element.addContent(position, new org.jdom.Text(text));
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@Issue.Pre(value = "jdom just returns false if it could not remove the attribute; XOM throws", resolved = true)
	@MapsTo("org.jdom.Element#removeAttribute(org.jdom.Attribute)")
	public Attribute removeAttribute(Attribute attribute) {
		if (attribute == null) {
			throw new NullPointerException("null attribute");
		}
		if (element.getAttributes().size() == 0) {
			throw new NoSuchAttributeException("no attributes; cannot remove " + attribute.getQualifiedName());
		}
		boolean removed = element
				.removeAttribute(((Attribute) attribute).attribute);
		if (!removed) {
			throw new NoSuchAttributeException("no such attribute: "
					+ attribute.getQualifiedName());
		}
		return attribute;
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#removeContent()")
	public Nodes removeChildren() {
		List kids = element.removeContent();
		for (Object kid: kids) {
			Node node = content2node((org.jdom.Content)kid);
			if (node instanceof Element) {
				((Element)node).setBaseURI(getBaseURI());
			}
		}
		return new Nodes(kids);
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.ADVANCED_DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#removeNamespaceDeclaration(org.jdom.Namespace)")
	public void removeNamespaceDeclaration(String prefix) {
		element.removeNamespaceDeclaration(element.getNamespace(prefix));
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@MapsTo("org.jdom.Element#setName(String)")
	public void setLocalName(String localName) {
		try {
			element.setName(localName);
		} catch (org.jdom.IllegalNameException e) {
			throw new IllegalNameException(e, localName);
		}
	}

	@Progress(value = Status.NEEDSWORK, comment = "")
	@Solution(value = Strategy.MACRO, comment = "")
	@Issue.Pre("XOM does more analysis on whether a call to this method is valid")
	@Unresolved(XML.Namespacing)
	@MapsTo("")
	public void setNamespacePrefix(String prefix) {
		// TODO: probably wrong; unsupported feature?
		try {
			element.setNamespace(org.jdom.Namespace.getNamespace(prefix,
					element.getNamespaceURI()));
		} catch (org.jdom.IllegalNameException e) {
			throw new IllegalNameException(e, prefix);
		}
	}

	@Progress(value = Status.NEEDSWORK, comment = "")
	@Solution(value = Strategy.MACRO, comment = "")
	@Issue.Pre("XOM does more analysis on whether a call to this method is valid")
	@Unresolved(XML.Namespacing)
	@MapsTo("")
	public void setNamespaceURI(String uri) {
		if (element.getNamespacePrefix().equals("")) {
			if (uri == null || uri.equals("")) {
				element.setNamespace(org.jdom.Namespace.NO_NAMESPACE);
				return ;
			}
		}
		else {
			if (uri == null || uri.equals("")) {
				throw new NamespaceConflictException("unsetting prefixed namespace");
			}
		}
		String error = org.jdom.Verifier.checkNamespaceURI(uri);
		if (error != null) {
			throw new MalformedURIException(error);
		}
		
		element.setNamespace(org.jdom.Namespace.getNamespace(element
				.getNamespacePrefix(), uri));
	}

	@Progress(value = Status.OK, comment = "")
	@Solution(value = Strategy.DELEGATE, comment = "")
	@Override
	@MapsTo("org.jdom.Element#equals(Object)")
	public boolean equals(Object o) {
		if (!(o instanceof Element)) {
			return false;
		}
		return element.equals(((Element) o).element);
	}

	@Progress(value = Status.DONTCARE, comment = "opaque")
	@Override
	@MapsTo("org.jdom.Element#hashCode()")
	public int hashCode() {
		return element.hashCode();
	}
}