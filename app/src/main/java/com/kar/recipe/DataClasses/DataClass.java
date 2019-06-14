package com.kar.recipe.DataClasses;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class DataClass {
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( this.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        for (Field field: this.getClass().getDeclaredFields())
            result.append(fieldToString(field));

        if (this.getClass().getSuperclass() != null) for ( Field field : this.getClass().getSuperclass().getDeclaredFields()  ) result.append(fieldToString(field));

        result.append("}");

        return result.toString();
    }

    private String fieldToString(Field field) {
        String newLine = System.getProperty("line.separator");
        StringBuilder result = new StringBuilder();
        if (!Modifier.isTransient(field.getModifiers())) {
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                field.setAccessible(true);
                result.append(field.get(this));
                field.setAccessible(false);
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
            result.append(newLine);
        }
        return result.toString();
    }
}
