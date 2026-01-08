package de.assecor.persons.model;

import com.fasterxml.jackson.annotation.JsonValue;
import de.assecor.persons.exception.InvalidColorException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Color {
  BLUE(1, "blau"),
  GREEN(2, "grün"),
  VIOLET(3, "violett"),
  RED(4, "rot"),
  YELLOW(5, "gelb"),
  CYAN(6, "türkis"),
  WHITE(7, "weiß");

  private static final Map<Integer, Color> BY_ID =
      Arrays.stream(values()).collect(Collectors.toMap(Color::getId, c -> c));
  private static final Map<String, Color> BY_NAME =
      Arrays.stream(values()).collect(Collectors.toMap(Color::getName, c -> c));

  private final int id;
  private final String name;

  Color(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  @JsonValue
  public String getName() {
    return name;
  }

  public static Color fromId(int id) {
    Color color = BY_ID.get(id);
    if (color == null) throw new InvalidColorException("Invalid Color id: " + id);
    return color;
  }

  public static Color fromName(String name) {
    Color color = BY_NAME.get(name.toLowerCase());
    if (color == null) throw new InvalidColorException("Invalid Color name: " + name);
    return color;
  }
}
