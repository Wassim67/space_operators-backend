package com.spaceoperators.model.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

public class OperationMessage {
    @JsonUnwrapped // pour sérialiser les champs de la classe Data comme s'ils étaient directement dans la classe OperationMessage
    private Data data;

    public OperationMessage(int turn, String role, String id, int duration, String description, List<Element> elements, Result result) {
        this.data = new Data(turn, role, id, duration, description, elements, result);
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private int turn;
        private String role;
        private String id;
        private int duration;
        private String description;
        private List<Element> elements;
        private Result result;

        public Data(int turn, String role, String id, int duration, String description, List<Element> elements, Result result) {
            this.turn = turn;
            this.role = role;
            this.id = id;
            this.duration = duration;
            this.description = description;
            this.elements = elements;
            this.result = result;
        }

        // Getters
        public int getTurn() {
            return turn;
        }

        public void setTurn(int turn) {
            this.turn = turn;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Element> getElements() {
            return elements;
        }

        public void setElements(List<Element> elements) {
            this.elements = elements;
        }

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }
    }

    public static class Element {
        private String type;
        private int id;
        private String valueType;
        private Object value;

        public Element(String type, int id, String valueType, Object value) {
            this.type = type;
            this.id = id;
            this.valueType = valueType;
            this.value = value;
        }

        // Getters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getValueType() {
            return valueType;
        }

        public void setValueType(String valueType) {
            this.valueType = valueType;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    public static class Result {
        private ButtonResult buttons;
        private List<Integer> switches;
        private List<List<Integer>> links;

        public Result(ButtonResult buttons, List<Integer> switches, List<List<Integer>> links) {
            this.buttons = buttons;
            this.switches = switches;
            this.links = links;
        }

        // Getters
        public ButtonResult getButtons() {
            return buttons;
        }

        public void setButtons(ButtonResult buttons) {
            this.buttons = buttons;
        }

        public List<Integer> getSwitches() {
            return switches;
        }

        public void setSwitches(List<Integer> switches) {
            this.switches = switches;
        }

        public List<List<Integer>> getLinks() {
            return links;
        }

        public void setLinks(List<List<Integer>> links) {
            this.links = links;
        }
    }

    public static class ButtonResult {
        private String order;
        private List<Integer> ids;

        public ButtonResult(String order, List<Integer> ids) {
            this.order = order;
            this.ids = ids;
        }

        // Getters
        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
        }

        public List<Integer> getIds() {
            return ids;
        }

        public void setIds(List<Integer> ids) {
            this.ids = ids;
        }
    }
}
