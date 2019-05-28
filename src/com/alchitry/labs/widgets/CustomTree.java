package com.alchitry.labs.widgets;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;

import com.alchitry.labs.gui.Images;
import com.alchitry.labs.gui.Theme;

public class CustomTree extends Canvas {
	private List<TreeElement> root = new ArrayList<>();
	private List<TreeElement> visibleNodes = new ArrayList<>();
	private TreeElement hoverElement;
	private TreeElement activeElement;
	private int textHeight;
	private int oldHeight;

	private enum MouseState {
		NONE, HOVER, CLICK_ACTIVE, CLICK_INACTIVE
	};

	private MouseState mouseState = MouseState.NONE;

	public CustomTree(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);


		setFont(Theme.defaultFont);
		
		textHeight = 21;
		oldHeight = -1;

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				CustomTree.this.paintControl(e);
			}
		});

		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				redraw();
			}

			@Override
			public void focusGained(FocusEvent e) {
				redraw();
			}
		});

		addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				TreeElement mouseEle = getElement(e.x, e.y);
				boolean newEle = hoverElement != mouseEle;
				switch (mouseState) {
				case NONE:
					mouseState = MouseState.HOVER;
				case HOVER:
					if (hoverElement != null && newEle) {
						redraw(hoverElement.bounds);
					}
					hoverElement = mouseEle;
					if (hoverElement != null && newEle) {
						redraw(hoverElement.bounds);
					}
					break;
				case CLICK_ACTIVE:
					if (newEle)
						mouseState = MouseState.CLICK_INACTIVE;
					break;
				case CLICK_INACTIVE:
					if (!newEle)
						mouseState = MouseState.CLICK_ACTIVE;
				}
			}
		});

		addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseExit(MouseEvent e) {
				switch (mouseState) {
				case HOVER:
				case NONE:
					if (hoverElement != null) {
						redraw(hoverElement.bounds);
						hoverElement = null;
					}
					mouseState = MouseState.NONE;
					break;
				case CLICK_ACTIVE:
				case CLICK_INACTIVE:
					mouseState = MouseState.CLICK_INACTIVE;
					break;
				}
			}

			@Override
			public void mouseEnter(MouseEvent e) {
				TreeElement mouseEle = getElement(e.x, e.y);
				switch (mouseState) {
				case NONE:
				case HOVER:
					if (mouseEle != null) {
						hoverElement = mouseEle;
					}
					mouseState = MouseState.HOVER;
					redraw();
					break;
				case CLICK_INACTIVE:
				case CLICK_ACTIVE:
					mouseState = MouseState.CLICK_ACTIVE;
					redraw(activeElement.getBounds());
					break;
				}
			}
		});
		addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				TreeElement mouseEle = getElement(e.x, e.y);
				if (mouseEle != null && mouseEle == activeElement) {
					activeElement = mouseEle;
					mouseState = MouseState.HOVER;

					if (activeElement.isNode()) {
						TreeNode activeNode = (TreeNode) activeElement;
						activeNode.setOpen(!activeNode.isOpen());
						updateTree();
						Event event = new Event();
						event.widget = CustomTree.this;
						notifyListeners(SWT.Resize, event);
						redraw();
					} else {
						redraw(activeElement.bounds);
					}
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				setFocus();
				TreeElement mouseEle = getElement(e.x, e.y);
				boolean newEle = mouseEle != activeElement;
				if (mouseEle != null) {
					if (activeElement != null && newEle) {
						redraw(activeElement.bounds);
					}
					activeElement = mouseEle;
					mouseState = MouseState.CLICK_ACTIVE;
					redraw(activeElement.bounds);
					if (e.button == 3) {
						mouseEle.clicked(CustomTree.this, e.button);
					}
				} else {
					if (e.button == 3) {
						for (MenuItem mi : getMenu().getItems())
							mi.dispose();
					}
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TreeElement mouseEle = getElement(e.x, e.y);
				if (mouseEle != null && !mouseEle.isNode() && e.button == 1) {
					mouseEle.clicked(CustomTree.this, e.button);
				}
			}
		});

		addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				updateBounds();
			}
		});
	}

	private void redraw(Rectangle r) {
		redraw(r.x, r.y, r.width, r.height, false);
	}

	private TreeElement getElement(int x, int y) {
		for (TreeElement e : visibleNodes) {
			if (e.bounds.contains(x, y)) {
				return e;
			}
		}
		return null;
	}

	private void paintControl(PaintEvent e) {
		GC gc = e.gc;
		textHeight = gc.stringExtent("T").y;
		if (oldHeight != textHeight)
			updateBounds();
		gc.setBackground(getBackground());
		Rectangle clientArea = getClientArea();
		gc.fillRectangle(clientArea);
		gc.setForeground(getForeground());
		for (TreeElement ele : visibleNodes) {
			boolean selected = false;
			int x = ele.getDepth() * 15 + 5;
			if (ele == activeElement) {
				selected = true;
				if (isFocusControl()) {
					gc.setBackground(Theme.treeSelectedFocusedColor);

				} else {
					gc.setBackground(Theme.treeSelectedColor);
				}
				gc.fillRectangle(ele.getBounds());
			} else if (ele == hoverElement) {
				gc.setBackground(Theme.treeHoverColor);
				gc.fillRectangle(ele.getBounds());
			}
			gc.setBackground(getBackground());

			if (ele.isNode() && ((TreeNode) ele).hasChildren()) {
				Image arrowIcon = selected ? Images.treeArrowIconSelected : Images.treeArrowIcon;
				int yOffset = ele.getBounds().y + (ele.getBounds().height - arrowIcon.getBounds().height) / 2 - 1;
				Transform oldTransform = new Transform(gc.getDevice());
				if (((TreeNode) ele).isOpen())
					setRotation(gc, x, yOffset, arrowIcon.getBounds(), 90);
				gc.drawImage(arrowIcon, x, yOffset);
				gc.setTransform(oldTransform);
			}

			if (selected)
				gc.setForeground(Theme.treeSelectedForegroundColor);
			else
				gc.setForeground(getForeground());
			gc.drawText(ele.getName(), x + 20, ele.getBounds().y + 2, true);
		}

	}

	private void setRotation(GC gc, int x, int y, Rectangle bounds, float angle) {
		Transform transform = new Transform(gc.getDevice());
		transform.translate(x + bounds.width / 2, y + bounds.height / 2);
		transform.rotate(angle);
		transform.translate(-x - bounds.width / 2, -y - bounds.height / 2);
		gc.setTransform(transform);
	}

	private void updateBounds() {
		oldHeight = textHeight;
		int y = 0;
		Rectangle clientArea = getClientArea();
		for (TreeElement te : root) {
			Iterator<TreeElement> itr = getTreeIterator(te, true);
			while (itr.hasNext()) {
				TreeElement ele = itr.next();
				Rectangle bounds = new Rectangle(0, y, clientArea.width, 6 + textHeight);
				ele.setBounds(bounds);
				y += 6 + textHeight;
			}
		}
	}

	// private int getChildCount(List<TreeElement> root, boolean openOnly) {
	// int i = 0;
	// for (TreeElement e : root)
	// i += getChildCount(e, openOnly);
	// return i;
	// }

	// private int getChildCount(TreeElement root, boolean openOnly) {
	// int i = 0;
	// Iterator<TreeElement> itr = getTreeIterator(root, openOnly);
	// while (itr.hasNext()) {
	// itr.next();
	// i++;
	// }
	// return i;
	// }

	private Iterator<TreeElement> getTreeIterator(final TreeElement root, final boolean openOnly) {
		return new Iterator<TreeElement>() {
			private TreeElement next = root;
			private TreeElement current;

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public TreeElement next() {
				current = next;
				TreeElement ptr = next;
				next = null;

				if (!ptr.isNode() || !((TreeNode) ptr).hasChildren() || (!((TreeNode) ptr).isOpen() && openOnly)) {
					while (true) {
						TreeNode parent = ptr.getParent();
						if (parent == null)
							return current;
						int idx = parent.getChildren().indexOf(ptr);
						if (idx + 1 >= parent.getChildren().size()) {
							ptr = parent;
							if (parent == root)
								return current;
						} else {
							next = parent.getChildren().get(idx + 1);
							return current;
						}
					}
				} else { // must be open node with children
					next = ((TreeNode) ptr).getChildren().get(0);
					return current;
				}
			}

			@Override
			public void remove() {
				if (current != null) {

					TreeElement ptr = current;
					next = null;

					while (true) {
						TreeNode parent = ptr.getParent();
						if (parent == null)
							break;
						int idx = parent.getChildren().indexOf(ptr);
						if (idx + 1 >= parent.getChildren().size()) {
							ptr = parent;
							if (parent == root)
								break;
						} else {
							next = parent.getChildren().get(idx + 1);
							break;
						}
					}

					TreeNode parent = current.getParent();
					if (parent != null)
						parent.remove(current);

					current.setParent(null);
				}
			}

		};
	}

	int getDepth(TreeElement e) {
		if (e.getParent() == null)
			return 0;
		else
			return 1 + getDepth(e.getParent());
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		// int openRows = getChildCount(root, true);

		GC gc = new GC(this);
		int maxWidth = 0;
		int height = 0;
		for (TreeElement te : root) {
			Iterator<TreeElement> itr = getTreeIterator(te, true);
			while (itr.hasNext()) {
				TreeElement ele = itr.next();
				height += ele.getBounds().height;
				Point textSize = gc.stringExtent(ele.getName());
				maxWidth = Math.max(textSize.x + 15 * getDepth(ele) + 20, maxWidth);
			}
		}
		gc.dispose();

		return new Point(maxWidth + 10, height);
	}

	public void updateTree() {
		visibleNodes.clear();
		for (TreeElement te : root) {
			Iterator<TreeElement> itr = getTreeIterator(te, true);
			while (itr.hasNext()) {
				TreeElement ele = itr.next();
				visibleNodes.add(ele);
				ele.setDepth(getDepth(ele));
			}
		}
		updateBounds();
	}

	public TreeElement getElement(int i) {
		return root.get(i);
	}

	public int getRootSize() {
		return root.size();
	}

	public void addElement(TreeElement ele) {
		root.add(ele);
	}

	public void removeAll() {
		root.clear();
	}

	public static abstract class TreeElement {
		private List<Listener> listeners = new ArrayList<>();
		private String name;
		private TreeNode parent;
		private Rectangle bounds;
		private int depth;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		abstract public boolean isNode();

		public TreeNode getParent() {
			return parent;
		}

		public void setParent(TreeNode parent) {
			this.parent = parent;
		}

		public void setBounds(Rectangle bounds) {
			this.bounds = bounds;
		}

		public Rectangle getBounds() {
			return bounds;
		}

		public void setDepth(int depth) {
			this.depth = depth;
		}

		public int getDepth() {
			return depth;
		}

		public void addClickListener(Listener listener) {
			listeners.add(listener);
		}

		public void clicked(CustomTree tree, int mouseButton) {
			Event e = new Event();
			e.data = this;
			e.widget = tree;
			e.button = mouseButton;
			for (Listener l : listeners)
				l.handleEvent(e);
		}
	}

	public static class TreeNode extends TreeElement {
		private List<TreeElement> children = new ArrayList<>();
		private boolean open;

		public TreeNode(String name) {
			setName(name);
		}

		@Override
		public boolean isNode() {
			return true;
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}

		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean open) {
			this.open = open;
		}

		public void add(TreeElement ele) {
			add(children.size(), ele);
		}

		public void add(int i, TreeElement ele) {
			if (ele.getParent() != null)
				throw new InvalidParameterException("Can't add TreeElements that already have a parent!");
			ele.setParent(this);
			children.add(i, ele);
		}

		public List<TreeElement> getChildren() {
			return children;
		}

		public void remove(int i) {
			children.get(i).setParent(null);
			children.remove(i);
		}

		public boolean remove(TreeElement ele) {
			if (children.remove(ele)) {
				ele.setParent(null);
				return true;
			}
			return false;
		}
	}

	public static class TreeLeaf extends TreeElement {
		private File file;
		
		public TreeLeaf(File file) {
			setName(file.getName());
			this.file = file;
		}
		
		public File getFile() {
			return file;
		}

		@Override
		public boolean isNode() {
			return false;
		}
	}
}
