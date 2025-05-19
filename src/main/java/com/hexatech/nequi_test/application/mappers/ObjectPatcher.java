package com.hexatech.nequi_test.application.mappers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectPatcher {
    private ObjectPatcher() {
    }

     public static <T> void patch(T existingEntity, T incompleteEntity) {
        try {
            Class<?> currentClass = existingEntity.getClass();
            List<Field> fields = new ArrayList<>();
            while (currentClass != null) {
                fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
                currentClass = currentClass.getSuperclass();
            }
            // Parchar los campos
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(incompleteEntity);
                if (value != null) {
                    field.set(existingEntity, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error patching entity", e);
        }
    }
}
