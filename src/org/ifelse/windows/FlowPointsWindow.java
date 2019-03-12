package org.ifelse.windows;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.ifelse.IEAppLoader;
import org.ifelse.RP;
import org.ifelse.model.MFlowPoint;
import org.ifelse.model.MFlowPointGroup;
import org.ifelse.ui.OnClickListener;
import org.ifelse.ui.ToolbarButton;
import org.ifelse.utils.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.util.List;

public class FlowPointsWindow implements ToolWindowFactory, DragGestureListener, DragSourceListener {


    JTree tree;

    Project project;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(createPanel(project), "", false);
        toolWindow.getContentManager().addContent(content);

        this.project = project;

    }

    private JComponent createPanel(Project project) {


        String path = RP.Path.getFlowPointsPath(project);

        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();


        ToolbarButton btn_edit = new ToolbarButton(AllIcons.ToolbarDecorator.Edit);
        ToolbarButton btn_refresh = new ToolbarButton(AllIcons.Actions.Refresh);


        btn_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(JComponent component) {

                Util.open(project,path);

            }
        });

        btn_refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(JComponent component) {
                initTree(project);
            }
        });

        toolBar.setFloatable(false);
        toolBar.addSeparator();
        toolBar.add(btn_edit);
        toolBar.add(btn_refresh);
        toolBar.addSeparator();

        panel.add(toolBar, BorderLayout.NORTH);

        tree = new JTree();

        JScrollPane scrollPane = new JScrollPane(tree);


        initTree(project);

        panel.add(scrollPane, BorderLayout.CENTER);



        DragSource dragSource = DragSource.getDefaultDragSource();

        dragSource.createDefaultDragGestureRecognizer(tree, // component where
                // drag originates
                DnDConstants.ACTION_COPY_OR_MOVE, // actions
                this);

        tree.setRowHeight(22);


        //tree.setComponentPopupMenu();

        //JPopupMenu menu = new JPopupMenu();
        JBPopupMenu menu = new JBPopupMenu("Points");

        AbstractAction item_addg = new AbstractAction("Add Group", AllIcons.ToolbarDecorator.AddIcon) {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
        AbstractAction item_addp = new AbstractAction("Add Point", AllIcons.ToolbarDecorator.AddIcon) {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };


//        menu.add( item_addg  );
//        menu.addSeparator();
//        menu.add( item_addp  );
//        tree.setComponentPopupMenu(menu);



        return panel;

    }


    private void initTree(Project project) {


        final   List<MFlowPointGroup> flowpoint_groups = IEAppLoader.getMProject(project).loadFlowPoints(project);//.flowpoint_groups;

        TreeModel tree_model = new TreeModel() {


            @Override
            public Object getRoot() {
                return "Points";
            }

            private boolean isRoot(Object parent) {

                return "Points".equals(parent);
            }

            @Override
            public Object getChild(Object parent, int index) {

                if (isRoot(parent)) {

                    return flowpoint_groups.get(index);

                } else {

                    if (parent instanceof MFlowPointGroup) {
                        return ((MFlowPointGroup) parent).points.get(index);
                    }


                }
                return null;


            }

            @Override
            public int getChildCount(Object parent) {

                if (isRoot(parent)) {

                    return flowpoint_groups.size();

                } else {

                    if (parent instanceof MFlowPointGroup) {

                        return ((MFlowPointGroup) parent).points.size();

                    }

                }
                return 0;
            }

            @Override
            public boolean isLeaf(Object node) {

                return (node instanceof MFlowPoint);

            }

            @Override
            public void valueForPathChanged(TreePath path, Object newValue) {

            }

            @Override
            public int getIndexOfChild(Object parent, Object child) {
                if (isRoot(parent))
                    return flowpoint_groups.size();
                else if (parent instanceof MFlowPointGroup) {

                    MFlowPointGroup group = (MFlowPointGroup) parent;
                    return group.points.indexOf(child);

                }
                return 0;
            }

            @Override
            public void addTreeModelListener(TreeModelListener l) {

            }

            @Override
            public void removeTreeModelListener(TreeModelListener l) {

            }
        };


        DefaultTreeCellRenderer tree_render = new DefaultTreeCellRenderer() {


            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {


                Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);


                if (value instanceof MFlowPoint) {


                    MFlowPoint me = (MFlowPoint) value;

                    BufferedImage img = null;
                    try {

                        //Log.console(RP.Path.data + me.iconpath);

                        //Log.console("component :%s",component);

                        String iconpath = RP.Path.getIconPath(project, me.icon);
                        img = IconFactory.createImage(iconpath, 20, 20);


                        ImageIcon icon = new ImageIcon(img);

                        setIcon(icon);


                    } catch (Exception e) {

                        e.printStackTrace();

                    }


                }

                return component;
            }

        };

        tree.setCellRenderer(tree_render);
        tree.setModel(tree_model);


    }


    @Override
    public void init(ToolWindow window) {
        window.setIcon(Icons.icon_logo);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {


        return true;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return true;
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {

        Point ptDragOrigin = dge.getDragOrigin();
        //int row = tree.getRowForLocation(ptDragOrigin.x, ptDragOrigin.y);


        TreePath tp = tree.getPathForLocation(ptDragOrigin.x, ptDragOrigin.y);

        if (tp == null)
            return;
        Object obj = tp.getLastPathComponent();

        if (obj instanceof MFlowPoint) {

            final MFlowPoint me = (MFlowPoint) obj;

            BufferedImage img = null;
            try {
                img = IconFactory.createImage(RP.Path.getIconPath( project,  me.icon) );

            } catch (Exception e) {
                e.printStackTrace();
            }

            dge.startDrag(
                    Cursor.getDefaultCursor(), // move_cursor
                    img, new Point(-20, -20),
                    new Transferable() {
                        @Override
                        public DataFlavor[] getTransferDataFlavors() {
                            return new DataFlavor[0];
                        }

                        @Override
                        public boolean isDataFlavorSupported(DataFlavor flavor) {
                            return false;
                        }

                        @Override
                        public Object getTransferData(DataFlavor flavor) {
                            return me;
                        }
                    },
                    this); //
        }


    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {

    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {

    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {

    }

    @Override
    public void dragExit(DragSourceEvent dse) {

    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {

    }


}
