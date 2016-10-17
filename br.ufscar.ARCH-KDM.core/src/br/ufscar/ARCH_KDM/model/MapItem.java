package br.ufscar.ARCH_KDM.model;

import java.util.ArrayList;

import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;

public class MapItem {
	
	private AbstractStructureElement structureElement;
	private ArrayList<AbstractCodeElement> codeElements;
	
	public MapItem () {
		this.codeElements = new ArrayList<AbstractCodeElement>(); 
	}

	public AbstractStructureElement getStructureElement() {
		return structureElement;
	}

	public void setStructureElement(AbstractStructureElement structureElement) {
		this.structureElement = structureElement;
	}

	public ArrayList<AbstractCodeElement> getCodeElements() {
		return codeElements;
	}

	public void setCodeElements(ArrayList<AbstractCodeElement> codeElements) {
		this.codeElements = codeElements;
	}
	
	

}
