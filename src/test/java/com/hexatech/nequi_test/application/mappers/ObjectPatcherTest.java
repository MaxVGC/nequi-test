package com.hexatech.nequi_test.application.mappers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

class ObjectPatcherTest {

    static class TestEntity {
        private String name;
        private Integer age;
        private String address;

        public TestEntity() {
        }

        public TestEntity(String name, Integer age, String address) {
            this.name = name;
            this.age = age;
            this.address = address;
        }

        // Getters for assertions
        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        public String getAddress() {
            return address;
        }
    }

    @Test
    void patch_shouldUpdateNonNullFields() {
        TestEntity existing = new TestEntity("Alice", 30, "Street 1");
        TestEntity patch = new TestEntity(null, 40, null);

        ObjectPatcher.patch(existing, patch);

        assertEquals("Alice", existing.getName());
        assertEquals(40, existing.getAge());
        assertEquals("Street 1", existing.getAddress());
    }

    @Test
    void patch_shouldUpdateAllFieldsIfAllNonNull() {
        TestEntity existing = new TestEntity("Alice", 30, "Street 1");
        TestEntity patch = new TestEntity("Bob", 25, "Street 2");

        ObjectPatcher.patch(existing, patch);

        assertEquals("Bob", existing.getName());
        assertEquals(25, existing.getAge());
        assertEquals("Street 2", existing.getAddress());
    }

    @Test
    void patch_shouldNotUpdateAnyFieldIfAllNull() {
        TestEntity existing = new TestEntity("Alice", 30, "Street 1");
        TestEntity patch = new TestEntity(null, null, null);

        ObjectPatcher.patch(existing, patch);

        assertEquals("Alice", existing.getName());
        assertEquals(30, existing.getAge());
        assertEquals("Street 1", existing.getAddress());
    }

    @Test
    void patch_shouldHandleNullFieldsGracefully() {
        TestEntity existing = new TestEntity(null, null, null);
        TestEntity patch = new TestEntity("Bob", 20, null);

        ObjectPatcher.patch(existing, patch);

        assertEquals("Bob", existing.getName());
        assertEquals(20, existing.getAge());
        assertNull(existing.getAddress());
    }

    static class ParentEntity {
        private String parentField;
    }

    static class ChildEntity extends ParentEntity {
        private String childField;
    }

    @Test
    void patch_shouldUpdateInheritedFields() {
        ChildEntity existing = new ChildEntity();
        ChildEntity patch = new ChildEntity();

        // Set via reflection since fields are private
        try {
            Field parentField = ParentEntity.class.getDeclaredField("parentField");
            Field childField = ChildEntity.class.getDeclaredField("childField");
            parentField.setAccessible(true);
            childField.setAccessible(true);

            parentField.set(existing, "parentOld");
            childField.set(existing, "childOld");

            parentField.set(patch, "parentNew");
            childField.set(patch, null);

            ObjectPatcher.patch(existing, patch);

            assertEquals("parentNew", parentField.get(existing));
            assertEquals("childOld", childField.get(existing));
        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }
    }
}