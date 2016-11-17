package br.ufscar.arch_kdm.ui.util;

import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public enum IconsType implements IIconsType{

	ADD{
		@Override
		public Image getImage() {
			return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/add.png"));
		}
	},
	ANNOTATION{
		@Override
		public Image getImage() {
			return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/annotation_obj.png"));
		}
	},
	CLASS{
		@Override
		public Image getImage() {
			return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/class_obj.gif"));
		}
	},
	ENUM{
		@Override
		public Image getImage() {
			return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/enum_obj.png"));
		}

	},
	ERROR{
		@Override
		public Image getImage() {
			return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/error_obj.png"));
		}

	},
	IMPORT{
		@Override
		public Image getImage() {
			return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/imp_obj.png"));
		}

	},
	INTERFACE{
		@Override
		public Image getImage() {
			return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/int_obj.png"));
		}

	},
	JAVA_FILE{
		@Override
		public Image getImage() {
			return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/jcu_obj.png"));
		}

	},
	PACKAGE{
		@Override
		public Image getImage() {
			return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/package_obj.gif"));
		}
	},
	REMOVE{
		@Override
		public Image getImage() {
			return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/remove.png"));
		}

	},
	SEARCH{
		@Override
		public Image getImage() {
			return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/search_obj.png"));
		}

	},
	STRUCTURAL_ELEMENT{
		@Override
		public Image getImage() {
			return new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/structuralElement.gif"));
		}

	}
	
	;

	public static Image getImageByElement(AbstractCodeElement abstractCodeElement) {
		if(abstractCodeElement instanceof Package){
			return IconsType.PACKAGE.getImage();
		}else if(abstractCodeElement instanceof ClassUnit){
			return IconsType.CLASS.getImage();
		}else if(abstractCodeElement instanceof InterfaceUnit){
			return IconsType.INTERFACE.getImage();
		}else if(abstractCodeElement instanceof EnumeratedType){
			return IconsType.ENUM.getImage();
		}
		return null;
	}
	
	
	
}
