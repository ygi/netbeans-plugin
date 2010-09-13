/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.php.nette.wizards.newpresenter;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.php.nette.utils.EditorUtils;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

public final class NewPresenterWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private int index;
    private WizardDescriptor wizard;
    private WizardDescriptor.Panel[] panels;

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        Project project = Templates.getProject(wizard);
        Sources sources = project.getLookup().lookup(Sources.class);
        SourceGroup[] groups = sources.getSourceGroups(Sources.TYPE_GENERIC);

        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                        Templates.buildSimpleTargetChooser(project, groups).bottomPanel(new NewPresenterParentPresenterWizardPanel()).create(),
                        new NewPresenterActionRenderWizardPanel(),
                    };
            String[] steps = createSteps();
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    private boolean isTemplateForGeneration() {
        Object[] actions = (Object[]) wizard.getProperty("actions");
        for (Object wholeAction : actions) {
            HashMap<String, Object> action = (HashMap<String, Object>) wholeAction;

            if ((Boolean) action.get("template")) {
                return true;
            }
        }

        return false;
    }

    public Set instantiate() throws IOException {
        FileObject dir = Templates.getTargetFolder(wizard);
        String targetName = Templates.getTargetName(wizard);
        DataFolder df = DataFolder.findFolder(dir);

        Object[] actions = (Object[]) wizard.getProperty("actions");
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("actions", actions);

        String parentPresenter = (String) wizard.getProperty("parentPresenter");
        hashMap.put("parentPresenter", parentPresenter);

        FileObject template = Templates.getTemplate(wizard);
        DataObject dTemplate = DataObject.find(template);
        DataObject dobj = dTemplate.createFromTemplate(df, targetName, hashMap);

        if (isTemplateForGeneration()) {
            String presenterName = EditorUtils.firstLetterCapital(targetName.replace("Presenter", ""));
            String templatesDirectory = (String) wizard.getProperty("templatesDirectory");

            File templatesDir = null;
            String latteTemplatePrefix = null;
            boolean dottedNotation = (Boolean) wizard.getProperty("dottedNotation");
            if (dottedNotation) {
                templatesDir = new File(templatesDirectory);
                latteTemplatePrefix = presenterName + ".";
            } else {
                templatesDir = new File(templatesDirectory + "/" + presenterName);
                templatesDir.mkdirs();
                latteTemplatePrefix = "";
            }

            FileObject foTemplatesDir = FileUtil.toFileObject(templatesDir);
            DataFolder templatesDf = DataFolder.findFolder(foTemplatesDir);

            FileObject latteTemplate = FileUtil.getConfigFile("Templates/Nette Framework/LatteTemplate.phtml");
            DataObject latteDTemplate = DataObject.find(latteTemplate);

            for (Object wholeAction : actions) {
                HashMap<String, Object> action = (HashMap<String, Object>) wholeAction;

                boolean generateTemplate = (Boolean) action.get("template");

                if (generateTemplate) {
                    String actionName = (String) action.get("name");
                    latteDTemplate.createFromTemplate(templatesDf, latteTemplatePrefix + EditorUtils.firstLetterSmall(actionName));
                }
            }

            FileUtil.refreshAll();
        }

        FileObject createdFile = dobj.getPrimaryFile();

        return Collections.singleton(createdFile);
    }

    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }

    public WizardDescriptor.Panel current() {
        return getPanels()[index];
    }

    public String name() {
        return index + 1 + ". from " + getPanels().length;
    }

    public boolean hasNext() {
        return index < getPanels().length - 1;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    public void addChangeListener(ChangeListener l) {
    }

    public void removeChangeListener(ChangeListener l) {
    }

    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then uncomment
    // the following and call when needed: fireChangeEvent();
    /*
    private Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
    public final void addChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.add(l);
    }
    }
    public final void removeChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.remove(l);
    }
    }
    protected final void fireChangeEvent() {
    Iterator<ChangeListener> it;
    synchronized (listeners) {
    it = new HashSet<ChangeListener>(listeners).iterator();
    }
    ChangeEvent ev = new ChangeEvent(this);
    while (it.hasNext()) {
    it.next().stateChanged(ev);
    }
    }
     */
    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
    private String[] createSteps() {
        String[] beforeSteps = null;
        Object prop = wizard.getProperty("WizardPanel_contentData");
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }

        if (beforeSteps == null) {
            beforeSteps = new String[0];
        }

        String[] res = new String[(beforeSteps.length - 1) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeSteps.length - 1)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = panels[i - beforeSteps.length + 1].getComponent().getName();
            }
        }
        return res;
    }
}
