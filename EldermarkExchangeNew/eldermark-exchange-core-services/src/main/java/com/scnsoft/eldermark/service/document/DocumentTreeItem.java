package com.scnsoft.eldermark.service.document;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DocumentTreeItem<T> {

    private final T value;
    private final List<DocumentTreeItem<T>> children;
    private final boolean isFolder;

    private DocumentTreeItem(T value, List<DocumentTreeItem<T>> children, boolean isFolder) {
        this.value = value;
        this.children = children;
        this.isFolder = isFolder;
    }

    public List<T> valuesList() {
        var result = new ArrayList<T>();
        result.add(getValue());
        getChildren().forEach(it -> extractAllValues(result, it));
        return result;
    }

    public List<T> childValuesList() {
        var result = new ArrayList<T>();
        getChildren().forEach(it -> extractAllValues(result, it));
        return result;
    }

    public List<DocumentTreeItem<T>> getChildren() {
        return children;
    }

    public T getValue() {
        return value;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public <R> DocumentTreeItem<R> map(
            Function<T, R> treeValueMapper,
            Function<T, R> leafValueMapper
    ) {
        if (isFolder()) {
            return DocumentTreeItem.folder(
                    treeValueMapper.apply(getValue()),
                    getChildren().stream()
                            .map(item -> item.map(treeValueMapper, leafValueMapper))
                            .collect(Collectors.toList())
            );
        } else {
            return DocumentTreeItem.file(leafValueMapper.apply(getValue()));
        }
    }


    public static <T> DocumentTreeItem<T> folder(T value, List<DocumentTreeItem<T>> children) {
        return new DocumentTreeItem<T>(value, children, true);
    }

    public static <T> DocumentTreeItem<T> file(T value) {
        return new DocumentTreeItem<>(value, List.of(), false);
    }

    private static <T> void extractAllValues(List<T> result, DocumentTreeItem<T> tree) {
        result.add(tree.getValue());
        tree.getChildren()
            .forEach(it -> extractAllValues(result, it));
    }
}
