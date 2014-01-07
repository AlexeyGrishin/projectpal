package io.github.alexeygrishin.pal.tools;

import org.junit.Test;
import static org.junit.Assert.*;

public class ModuleTest {

    @Test
    public void shall_accept_body_with_section_ids() {
        new Module(":section\n123\n:section2\n456");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shall_reject_body_without_section_ids() {
        new Module("123");
    }

    @Test
    public void shall_correctly_parse_single_section() {
        Module module = new Module(":section\n12345");
        assertEquals(":section\n12345", module.getSection("section", null));
    }

    @Test
    public void shall_correctly_parse_two_sections() {
        Module module = new Module(":section\n" +
                "12345\n" +
                " /*:section2\n" +
                "*/\n" +
                "7890");
        assertEquals(":section\n12345\n", module.getSection("section", null));
        assertEquals(" /*:section2\n*/\n7890", module.getSection("section2", null));

    }

    @Test
    public void shall_correctly_parse_section_with_id() {
        Module module = new Module(":section test\n123");
        assertNull(module.getSection("section", null));
        assertNull(module.getSection("section", "a"));
        assertEquals(":section test\n" +
                "123", module.getSection("section", "test"));
    }

    @Test
    public void shall_correctly_parse_two_sections_same_type_different_ids() {
        Module module = new Module(":section test\n123\n*:section bob\nqwer");
        assertEquals(":section test\n123\n", module.getSection("section", "test"));
        assertEquals("*:section bob\nqwer", module.getSection("section", "bob"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void shall_reject_sections_with_same_types_and_ids() {
        new Module(":section test\n1\n*:section test\n2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shall_reject_sections_with_same_types_and_null_ids() {
        new Module(":section\n1\n*:section\n2");
    }

    @Test
    public void shall_correctly_parse_empty_section() {
        Module module = new Module(":section1\n1\n:empty\n:section2\n3");
        assertEquals(":empty\n", module.getSection("empty", null));
    }

    @Test
    public void shall_correctly_compose_class_body_back() {
        Module module = new Module(":section1\n1\n:empty\n:section2\n3");
        assertEquals(":section1\n1\n:empty\n:section2\n3", module.toString());
    }

}
