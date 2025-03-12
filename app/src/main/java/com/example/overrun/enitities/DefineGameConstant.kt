package com.example.gohero.enitities

import androidx.annotation.DrawableRes
import com.example.overrun.R

/**
 * Contains various game-related constants such as default hero attributes, screen sizing,
 * and collision offsets.
 */
object GameConstant {
    // Hero Attribute
    // All values are in Pixels
    const val DEFAULT_LIVES = 5U
    const val DEFAULT_HERO_SPEED = 50U
    const val INTERACT_FILER_INTERVAL_MS = 100U

    // For scaling
    const val GAME_SCREEN_COL = 7f  // e.g., 7.5f
    const val GAME_SCREEN_ROW = 14f // e.g., 15.3f
    const val DEFAULT_SCREEN_WIDTH_PIXEL = 1080U  // a Medium Phone API 35 width pixel
    const val DEFAULT_SCREEN_HEIGHT_PIXEL = 2211U // a Medium Phone API 35 height pixel

    const val HERO_CHARACTER_SPRITE_WIDTH_PIXEL = 144U
    const val HERO_CHARACTER_SPRITE_HEIGHT_PIXEL = 144U

    const val DEFAULT_INTERACT_SIZE_EXTEND_RATIO = 1.4375f

    // For record, keep here
    // const val DEFAULT_OBJECT_SIZE = 144U  // e.g., 9x from 16 pixels = 144U (Medium Phone)
    // const val DEFAULT_CHARACTER_INTERACT_SIZE = 207U  // e.g., 9x from 23 pixels

    // Offset
    //  -ve : shrink the other box
    //  +ve : enlarge the other box
    const val MOVE_COLLIDE_OFFSET_X = -10
    const val MOVE_COLLIDE_OFFSET_Y = -10
}

/**
 * Defines the various object types in the game world.
 *
 * - eCHARACTER (CharacterType)
 *     - eHero (HeroType)
 *         - eHero_Tokage
 *     - eSLIME
 *     - ePARROT
 * - eTREE
 * - eWALL
 * - eROCK
 * - ePATH
 * - eGRASS
 */
enum class eObjectType(val value: Int) {

    eNA(-1),
    eGRASS(0),
    eTREE(1),
    eROCK(2),
    eROCK_1(21),
    eWALL(3),
    ePATH(4),

    // Additional path/terrain/foliage objects
    ePATH_RANDOM_3(22),
    ePATH_BLANK_MUD(23),
    ePATH_LEFT_BOUNDARY(24),
    ePATH_RIGHT_BOUNDARY(25),
    ePATH_RANDOM(26),
    ePATH_RANDOM_2(27),
    eTREE_28(28),
    eMUSHROOMS(29),
    eROCKY_PATCH(30),
    eGRASS_BLANK(31),
    eGRASS_NORMAL(32),
    eGRASS_FLOWERS(33),
    eTREE_YELLOW(34),

    // Water-related tiles
    eWATER_TOP_CENTER(35),
    eWATER_TOP_LEFT(36),
    eWATER_TOP_RIGHT(37),
    eWATER_BOTTOM_CENTER(38),
    eWATER_BOTTOM_LEFT(39),
    eWATER_CENTER(40),
    eWATER_CENTER_LEFT(41),
    eWATER_CENTER_RIGHT(42),
    eWATER_LOW_RIGHT(43),

    // Character base type
    eCHARACTER(99);

    /**
     * Determines if an object is interactable (e.g., can respond to collisions distinctly).
     */
    fun isInteractable(): Boolean {
        return when (this) {
            eROCK_1 -> true
            else -> false
        }
    }

    companion object {
        /**
         * Retrieves an eObjectType instance by its integer value.
         */
        fun fromValue(value: Int): eObjectType {
            return when (value) {
                -1 -> eNA
                0 -> eGRASS
                1 -> eTREE
                2 -> eROCK
                21 -> eROCK_1
                3 -> eWALL
                4 -> ePATH

                22 -> ePATH_RANDOM_3
                23 -> ePATH_BLANK_MUD
                24 -> ePATH_LEFT_BOUNDARY
                25 -> ePATH_RIGHT_BOUNDARY
                26 -> ePATH_RANDOM
                27 -> ePATH_RANDOM_2
                28 -> eTREE_28
                29 -> eMUSHROOMS
                30 -> eROCKY_PATCH
                31 -> eGRASS_BLANK
                32 -> eGRASS_NORMAL
                33 -> eGRASS_FLOWERS
                34 -> eTREE_YELLOW
                35 -> eWATER_TOP_CENTER
                36 -> eWATER_TOP_LEFT
                37 -> eWATER_TOP_RIGHT
                38 -> eWATER_BOTTOM_CENTER
                39 -> eWATER_BOTTOM_LEFT
                40 -> eWATER_CENTER
                41 -> eWATER_CENTER_LEFT
                42 -> eWATER_CENTER_RIGHT
                43 -> eWATER_LOW_RIGHT

                99 -> eCHARACTER
                else -> throw IllegalArgumentException("Invalid argument $value")
            }
        }
    }

    /**
     * A user-friendly name for this type of object.
     */
    override fun toString(): String {
        return when (this) {
            eNA -> "Not Applicable"
            eGRASS -> "Grass"
            eTREE -> "Tree"
            eROCK -> "Rock"
            eROCK_1 -> "Rock 1"
            eWALL -> "Wall"
            ePATH -> "Path"
            ePATH_RANDOM_3 -> "Random Path Tile 3"
            ePATH_BLANK_MUD -> "Blank Mud Path"
            ePATH_LEFT_BOUNDARY -> "Path Left Boundary"
            ePATH_RIGHT_BOUNDARY -> "Path Right Boundary"
            ePATH_RANDOM -> "Random Path Tile"
            ePATH_RANDOM_2 -> "Random Path Tile 2"
            eTREE_28 -> "Tree 28"
            eMUSHROOMS -> "Mushrooms"
            eROCKY_PATCH -> "Rocky Patch"
            eGRASS_BLANK -> "Blank Grass"
            eGRASS_NORMAL -> "Normal Grass"
            eGRASS_FLOWERS -> "Grass with Flowers"
            eTREE_YELLOW -> "Yellow Tree"
            eWATER_TOP_CENTER -> "Water Top Center"
            eWATER_TOP_LEFT -> "Water Top Left"
            eWATER_TOP_RIGHT -> "Water Top Right"
            eWATER_BOTTOM_CENTER -> "Water Bottom Center"
            eWATER_BOTTOM_LEFT -> "Water Bottom Left"
            eWATER_CENTER -> "Water Center"
            eWATER_CENTER_LEFT -> "Water Center Left"
            eWATER_CENTER_RIGHT -> "Water Center Right"
            eWATER_LOW_RIGHT -> "Water Low Right"
            eCHARACTER -> "Character"
        }
    }
}

/**
 * Character types under the main umbrella of eCHARACTER object type.
 */
enum class eCharacterType {
    eNA,
    eHERO,
    eSLIME,
    ePARROT;

    override fun toString(): String {
        return when (this) {
            eHERO -> "Hero"
            eSLIME -> "Slime"
            ePARROT -> "Parrot"
            else -> "Invalid Character"
        }
    }
}

/**
 * Specific hero skins or hero variants.
 */
enum class eHeroType(@DrawableRes val resId: Int) {
    eHERO_TOKAGE(R.drawable.hero_tokage);
    // You can add more hero types here if needed
}

/**
 * Directions for character or object movement (e.g., up, down, left, right).
 */
enum class eDirection(val value: Int) {
    eDOWN(0),
    eUP(1),
    eLEFT(2),
    eRIGHT(3);

    companion object {
        /**
         * Retrieves eDirection instance by integer value.
         */
        fun fromValue(value: Int): eDirection {
            return when (value) {
                0 -> eDOWN
                1 -> eUP
                2 -> eLEFT
                3 -> eRIGHT
                else -> throw IllegalArgumentException("Invalid argument $value")
            }
        }
    }
}

/**
 * Defines various stages in the game.
 */
enum class eGameStage {
    eStage1,
    eStage2,
    eStage3,
    eTotalGameStage;

    override fun toString(): String {
        return when (this) {
            eStage1 -> "Stage 1"
            eStage2 -> "Stage 2"
            eStage3 -> "Stage 3"
            else -> "Invalid Stage"
        }
    }
}
