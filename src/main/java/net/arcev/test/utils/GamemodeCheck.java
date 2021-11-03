package net.arcev.test.utils;

import org.bukkit.GameMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command must have the given game mode
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GamemodeCheck {

    /**
     * The required game mode
     *
     * @return Required game mode
     */
    GameMode value();

}
