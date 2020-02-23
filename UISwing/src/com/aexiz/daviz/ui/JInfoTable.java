package com.aexiz.daviz.ui;

import com.aexiz.daviz.ui.InfoModel.PropertyModel;
import com.aexiz.daviz.ui.plaf.InfoTableUI;
import com.aexiz.daviz.ui.plaf.basic.BasicInfoTableUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JInfoTable extends JComponent {

    public static final String CLIENT_PROPERTY_MODEL = "model";
    public static final String CLIENT_PROPERTY_KIND = "kind";
    public static final String KIND_LABEL = "label";
    public static final String KIND_VALUE = "value";
    public static final String KIND_FILLER = "filler";
    static final String UICLASSID = "BasicInfoUI";
    private static final long serialVersionUID = -1127997595981620390L;

    static {
        UIDefaults def = UIManager.getDefaults();
        if (def.get(UICLASSID) == null)
            def.put(UICLASSID, BasicInfoTableUI.class.getName());
    }

    protected InfoModel model;
    protected ChangeListener changeListener;

    protected transient ChangeEvent changeEvent;
    private Handler handler;

    public JInfoTable() {
        setOpaque(true);
        setModel(new DefaultInfoModel());
        updateUI();
    }

    public InfoModel getModel() {
        return model;
    }

    public void setModel(InfoModel newModel) {
        InfoModel oldModel = getModel();
        removeSubComponents();
        if (oldModel != null) {
            oldModel.removeChangeListener(changeListener);
            changeListener = null;
        }
        model = newModel;
        if (newModel != null) {
            changeListener = createChangeListener();
            newModel.addChangeListener(changeListener);
        }
        firePropertyChange("model", oldModel, newModel);
        if (newModel != oldModel) {
            updateSubComponents();
        }
    }

    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    public ChangeListener[] getChangeListeners() {
        return listenerList.getListeners(ChangeListener.class);
    }

    protected void fireStateChanged() {
        if (changeEvent == null)
            changeEvent = new ChangeEvent(this);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ChangeListener.class)
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
    }

    public void updateUI() {
        setUI((InfoTableUI) UIManager.getUI(this));
    }

    public InfoTableUI getUI() {
        return (InfoTableUI) ui;
    }

    public void setUI(InfoTableUI ui) {
        removeSubComponents();
        super.setUI(ui);
        updateSubComponents();
    }

    public String getUIClassID() {
        return UICLASSID;
    }

    public int getVisibleIndexCount() {
        class Procedure {
            int curSize = 0;

            void compute(PropertyModel[] properties) {
                for (int i = 0; i < properties.length; i++) {
                    if (properties[i].getTitle().length() == 0) continue;
                    if (properties[i].getType() instanceof InfoModel.SimplePropertyType) {
                        curSize++;
                    } else if (properties[i].getType() instanceof InfoModel.CompoundPropertyType) {
                        curSize++;
                        try {
                            PropertyModel[] nested = (PropertyModel[]) properties[i].getValue();
                            for (PropertyModel n : nested) {
                                if (n.getParent() != properties[i]) throw new Exception();
                            }
                            compute(nested);
                        } catch (Exception ex) {
                            // Ignore whole property contents
                        }
                    } else throw new Error();
                }
            }
        }
        if (model == null) return 0;
        PropertyModel[] properties = model.getProperty();
        for (PropertyModel nested : properties) {
            // Ignore invalid models
            if (nested.getParent() != model) return 0;
        }
        Procedure p = new Procedure();
        p.compute(properties);
        return p.curSize;
    }

    public int getVisibleIndex(PropertyModel property) {
        if (property.getTitle().length() == 0)
            throw new UnsupportedOperationException();
        class Procedure {
            int curSize = 0;
            int index = -1;

            void compute(PropertyModel[] properties) {
                for (int i = 0; i < properties.length; i++) {
                    if (properties[i].getTitle().length() == 0) continue;
                    if (properties[i] == property) {
                        index = curSize;
                        break;
                    }
                    if (properties[i].getType() instanceof InfoModel.SimplePropertyType) {
                        curSize++;
                    } else if (properties[i].getType() instanceof InfoModel.CompoundPropertyType) {
                        curSize++;
                        try {
                            PropertyModel[] nested = (PropertyModel[]) properties[i].getValue();
                            for (PropertyModel n : nested) {
                                if (n.getParent() != properties[i]) throw new Exception();
                            }
                            compute(nested);
                            if (index >= 0)
                                break;
                        } catch (Exception ex) {
                            // Ignore whole property contents
                        }
                    } else throw new Error();
                }
            }
        }
        if (model == null) return -1;
        PropertyModel[] properties = model.getProperty();
        for (PropertyModel nested : properties) {
            // Ignore invalid models
            if (nested.getParent() != model) return -1;
        }
        Procedure p = new Procedure();
        p.compute(properties);
        return p.index;
    }

    private void removeSubComponents() {
        int n = getComponentCount();
        for (int i = 0; i < n; i++) {
            // Component c = getComponent(i);
            remove(i--);
            n--;
        }
    }

    private void updateSubComponents() {
        removeSubComponents();
        InfoModel model = getModel();
        if (model == null) return;
        PropertyModel[] properties = model.getProperty();
        for (PropertyModel p : properties) {
            // Ignore invalid models
            if (p.getParent() != model) return;
        }
        updateSubComponents_p0(properties);
        // Always add a filler label
        InfoTableUI ui = getUI();
        JComponent filler = createFillerComponent();
        filler.putClientProperty(CLIENT_PROPERTY_KIND, KIND_FILLER);
        if (ui != null) {
            ui.configureFillerComponent(filler);
            ui.addComponent(this, filler);
        } else {
            add(filler);
        }
        revalidate();
    }

    private void updateSubComponents_p0(PropertyModel[] properties) {
        InfoTableUI ui = getUI();
        for (PropertyModel p : properties) {
            String title = p.getTitle();
            if (title.length() == 0) continue;
            JLabel label = createLabelComponent(title);
            label.putClientProperty(CLIENT_PROPERTY_MODEL, p);
            label.putClientProperty(CLIENT_PROPERTY_KIND, KIND_LABEL);
            if (ui != null) {
                ui.configureLabelComponent(label);
                ui.addComponent(this, label);
            } else {
                add(label);
            }
            if (p.getType() instanceof InfoModel.SimplePropertyType) {
                String value = p.getValue().toString(); // String.toString() = this
                JTextField field = createSimplePropertyComponent(value);
                field.putClientProperty(CLIENT_PROPERTY_MODEL, p);
                field.putClientProperty(CLIENT_PROPERTY_KIND, KIND_VALUE);
                if (ui != null) {
                    ui.configureSimplePropertyComponent(field);
                    ui.addComponent(this, field);
                } else {
                    add(field);
                }
            } else if (p.getType() instanceof InfoModel.CompoundPropertyType) {
                PropertyModel[] nested;
                try {
                    nested = (PropertyModel[]) p.getValue();
                    for (PropertyModel n : nested) {
                        if (n.getParent() != p) throw new Exception();
                    }
                } catch (Exception ex) {
                    continue;
                }
                // Find special key under properties
                PropertyModel special = null;
                for (PropertyModel n : nested) {
                    if (n.getTitle().length() == 0) {
                        special = n;
                        break;
                    }
                }
                if (special == null) {
                    JComponent placeholder = createNestedPlaceholderComponent();
                    placeholder.putClientProperty(CLIENT_PROPERTY_MODEL, p);
                    placeholder.putClientProperty(CLIENT_PROPERTY_KIND, KIND_VALUE);
                    if (ui != null) {
                        ui.configureNestedPlaceholderComponent(placeholder);
                        ui.addComponent(this, placeholder);
                    } else {
                        add(placeholder);
                    }
                } else {
                    String value = special.getValue().toString();
                    JTextField field = createSimplePropertyComponent(value);
                    field.putClientProperty(CLIENT_PROPERTY_MODEL, p);
                    field.putClientProperty(CLIENT_PROPERTY_KIND, KIND_VALUE);
                    if (ui != null) {
                        ui.configureSimplePropertyComponent(field);
                        ui.addComponent(this, field);
                    } else {
                        add(field);
                    }
                }
                updateSubComponents_p0(nested);
            } else throw new Error();
        }
    }

    protected JLabel createLabelComponent(String name) {
        JLabel result = new JLabel(name);
        return result;
    }

    protected JTextField createSimplePropertyComponent(String value) {
        JTextField result = new JTextField();
        result.setText(value);
        result.setEditable(false);
        return result;
    }

    protected JComponent createNestedPlaceholderComponent() {
        return new JLabel();
    }

    protected JComponent createFillerComponent() {
        return new JLabel();
    }

    protected ChangeListener createChangeListener() {
        return getHandler();
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    class Handler implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent arg0) {
            updateSubComponents();
            revalidate();
            repaint();
            fireStateChanged();
        }

    }

}
