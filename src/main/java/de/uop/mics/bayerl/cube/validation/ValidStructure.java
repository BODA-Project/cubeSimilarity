package de.uop.mics.bayerl.cube.validation;

import de.uop.mics.bayerl.cube.model.Component;
import de.uop.mics.bayerl.cube.model.Cube;
import de.uop.mics.bayerl.cube.model.StructureDefinition;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by sebastianbayerl on 24/07/15.
 */
public class ValidStructure {

    private final static Logger LOG = Logger.getLogger(ValidStructure.class);

    public static boolean validate(Cube cube) {
        boolean valid = true;

        // check if there is minimal valid information
        if (cube == null || cube.getId() == null ||
                cube.getId().isEmpty())  {
            LOG.error("Cube has no ID");
            throw new IllegalStateException("Cube has no ID");
        }

        // check if there is a label, description and structure definition
        if (cube.getLabel() == null || cube.getLabel().isEmpty()) {
            LOG.warn(cube.getId() + ": No label found");
            valid = false;
        } else if (cube.getDescription() == null || cube.getDescription().isEmpty()) {
            LOG.warn(cube.getId() + ": No description found");
            valid = false;
        } else if (cube.getStructureDefinition() == null) {
            LOG.warn(cube.getId() + ": No structure definition found");
            valid = false;
        }

        // check if there are dimensions
        if (valid) {
            StructureDefinition sd = cube.getStructureDefinition();
            if (sd.getDimensions() == null || sd.getDimensions().size() == 0) {
                LOG.warn(cube.getId() + ": No dimensions found");
                valid = false;
            }
        }

        // check if the components have labels and urls
        if (valid) {
            StructureDefinition sd = cube.getStructureDefinition();
            Set<Component> components = new HashSet<>();
            components.addAll(sd.getDimensions());
            components.addAll(sd.getMeasures());

            for (Component c : components) {
                if (c.getLabel() == null || c.getLabel().isEmpty()
                        || c.getConcept() == null || c.getConcept().isEmpty()) {
                    valid = false;
                    LOG.warn(cube.getId() + ": Component label/url not found");
                    break;
                }
            }
        }

        // check if component urls are unique
        if (valid) {
            StructureDefinition sd = cube.getStructureDefinition();
            Set<String> urls = sd.getDimensions().stream().map(Component::getConcept).collect(Collectors.toSet());
            urls.addAll(sd.getMeasures().stream().map(Component::getConcept).collect(Collectors.toList()));

            if (urls.size() != sd.getDimensions().size() + sd.getMeasures().size()) {
                valid = false;
                LOG.warn(cube.getId() + ": Component urls are not unique.");
            }
        }

        return valid;
    }

}
