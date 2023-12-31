package org.wallentines.midnightcore.api.text;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

public class MClickEvent {

    private final ClickAction action;
    private final String value;

    public MClickEvent(ClickAction action, String value) {
        this.action = action;
        this.value = value;
    }

    public ClickAction getAction() {
        return action;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "MClickEvent{" +
                "action=" + action +
                ", value='" + value + '\'' +
                '}';
    }

    public static final Serializer<MClickEvent> SERIALIZER = ObjectSerializer.create(
            InlineSerializer.of(ClickAction::getId, ClickAction::byId).entry("action", MClickEvent::getAction),
            Serializer.STRING.entry("value", MClickEvent::getValue),
            MClickEvent::new);

    public enum ClickAction {

        OPEN_URL("open_url"),
        RUN_COMMAND("run_command"),
        SUGGEST_COMMAND("suggest_command"),
        CHANGE_PAGE("change_page"),
        COPY_TO_CLIPBOARD("copy_to_clipboard");

        private final String id;

        ClickAction(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static ClickAction byId(String id) {
            for(ClickAction t : values()) {
                if(t.id.equals(id)) return t;
            }
            return null;
        }
    }

}
