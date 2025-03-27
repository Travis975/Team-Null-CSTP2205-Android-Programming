package com.example.overrun.enitities

import androidx.annotation.DrawableRes
import com.example.overrun.R

// Contains various game-related constants such as default hero attributes, screen sizing,
// and collision offsets.
object GameConstant{
    // Hero Attribute
    // All value are in Pixels
    const val DEFAULT_LIVES = 5U
    const val DEFAULT_HERO_SPEED = 3U
    const val DEFAULT_HERO_REPEL_SPEED = 80U
    const val DEFAULT_HERO_HURT_INVINCIBLE_CYCLE = 3
    const val INTERACT_FILER_INTERVAL_MS = 100U
    const val HERO_CHARACTER_SPRITE_WIDTH_PIXEL = 144U
    const val HERO_CHARACTER_SPRITE_HEIGHT_PIXEL = 144U

    // Enemy Attribute
    const val DEFAULT_ENEMY_LIVES = 1U
    const val DEFAULT_ENEMY_SPEED = 10U
    const val DEFAULT_ENEMY_REPEL_SPEED = 60U
    const val DEFAULT_ENEMY_HURT_INVINCIBLE_CYCLE = 3
    const val ENEMY_CHARACTER_SPRITE_WIDTH_PIXEL = 144U
    const val ENEMY_CHARACTER_SPRITE_HEIGHT_PIXEL = 144U

    // for scaling
    const val GAME_SCREEN_COL = 7f //7.5f
    const val GAME_SCREEN_ROW = 14f //15.3f
    const val DEFAULT_SCREEN_WIDTH_PIXEL = 1080U        // a Medium Phone API 35 width pixel
    const val DEFAULT_SCREEN_HEIGHT_PIXEL = 2211U       // a Medium Phone API 35 height pixel

    const val DEFAULT_INTERACT_SIZE_EXTEND_RATIO = 1.4375f

    // For record, keep here
    //const val DEFAULT_OBJECT_SIZE = 144U // 96U // 144U    // x 9 from 16 pixels = 144U (Medium Phone),  x 6 from 16 pixels = 96U (nokia 2.4)
    //const val DEFAULT_CHARACTER_INTERACT_SIZE = 207U // 138U // 207U // x 9 from 23 pixels, ,  x 6 from 23 pixels (nokia 2.4)

    // Offset -ve : shrink the other box
    // Offset +ve : enlarge the other box
    const val MOVE_COLLIDE_OFFSET_X = -30
    const val MOVE_COLLIDE_OFFSET_Y = -30

    const val BE_INTERACT_COLLIDE_OFFSET_X = 15
    const val BE_INTERACT_COLLIDE_OFFSET_Y = 15
}

// ObjectType
//      |_ eCHARACTER (CharacterType)
//                    |_  eHero (HeroType)
//                          |_ eHero_Tokage
//                    |_  Slime
//                    |_  Parrot
//       |_ eTREE_BACKGROUND, eTREE
//       |_ eWALL
//       |_ eROCK
//       |_ ePATH
//       |_ eGRASS

enum class eObjectType(val value: Int){

    // Original new-script entries:
    eNA(-1),
    eGRASS(0),
    eTREE_BACKGROUND(1), eTREE(11),
    eROCK(2),
    eROCK_1(21), eROCK_TOXIC(22), eROCK_2(23),
    eWALL(3),
    ePATH(4),
    eSAND(5),
    eCACTUS(6),

    //Additional trees
    eTREE_YELLOW(13),
    eTREE_28(12),

    // Additional paths
    ePATH_RANDOM_3(41),
    ePATH_BLANK_MUD(42),
    ePATH_LEFT_BOUNDARY(43),
    ePATH_RIGHT_BOUNDARY(44),
    ePATH_RANDOM(45),
    ePATH_RANDOM_2(46),

    //Addititonal foilage, grass or plants
    eMUSHROOMS(34),
    eROCKY_PATCH(30),
    eGRASS_BLANK(31),
    eGRASS_NORMAL(32),
    eGRASS_FLOWERS(33),


    // Water-related tiles (from the old script):
    eWATER_TOP_CENTER(50),
    eWATER_TOP_LEFT(51),
    eWATER_TOP_RIGHT(52),
    eWATER_BOTTOM_CENTER(53),
    eWATER_BOTTOM_LEFT(54),
    eWATER_CENTER(55),
    eWATER_CENTER_LEFT(56),
    eWATER_CENTER_RIGHT(57),
    eWATER_LOW_RIGHT(58),

    eENEMY(98),
    eCHARACTER(99);

    public fun isStatic() : Boolean
    {
        return when(this){
            eGRASS, eTREE_BACKGROUND, eGRASS_NORMAL,ePATH, ePATH_RANDOM,
            ePATH_RANDOM_2, ePATH_RANDOM_3, eGRASS_FLOWERS, eROCKY_PATCH,
            eSAND->true
            else->false
        }
    }

    // Able to be interact or able to interact hero
    public fun isInteractable() : Boolean
    {
        return when(this){
            eROCK_1, eROCK_TOXIC, eCACTUS, eENEMY->true
            else->false
        }
    }

    // Once interact to hero happen, use the objectType to
    // check whether it is harmful to hero on reducing the life
    public fun isHarmful(): Boolean
    {
        return when(this){
            eROCK_TOXIC, eCACTUS, eENEMY->true
            else->false
        }
    }

    companion object{

        fun fromValue(value: Int?): eObjectType? {
            return eObjectType.entries.find { it.value == value }
        }

        fun fromIDStringToObjType(id : String) : eObjectType? {
            return id.split("_")[0].toIntOrNull()?.let{fromValue(it)}
        }
    }

    override fun toString(): String {
        return when(this){
            // Retaining both old and new naming for consistency
            eNA -> "Not Applicable"
            eGRASS -> "Grass"
            eTREE_BACKGROUND -> "Tree_Background"
            eTREE -> "Tree"
            eROCK -> "Rock"
            eROCK_1 -> "Rock 1"
            eROCK_2 -> "Rock 2"
            eWALL -> "Wall"
            ePATH -> "Path"
            eSAND -> "Sand"
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
            eENEMY->"Enemy"
            eCHARACTER->"Character"
            else->"Invalid Object"
        }
    }
}

// Character types under the main umbrella of eCHARACTER object type.
enum class eCharacterType{

    eNA, eHERO, eENEMY;

    override fun toString(): String {
        return when(this){
            eHERO->"Hero"
            eENEMY->"Enemy"
            else->"Invalid Character"
        }
    }
}

// Specific hero skins or hero variants.
enum class eHeroType(@DrawableRes val resId : Int){
    eHERO_TOKAGE(R.drawable.hero_tokage);
    // You can add more hero types here if needed
}

enum class eEnemyType(@DrawableRes val resId : Int){
    eENEMY_PARROT(R.drawable.parrot);
    // You can add more hero types here if needed
}

// Directions for character or object movement (e.g., up, down, left, right).
enum class eDirection(val value: Int){
    eDOWN(0), eUP(1), eLEFT(2), eRIGHT(3);

    companion object{
        // Retrieves eDirection instance by integer value.
        fun fromValue(value : Int) : eDirection{
            return when(value){
                0->eDOWN
                1->eUP
                2->eLEFT
                3->eRIGHT
                else->throw IllegalArgumentException("Invalid argument ${value}")
            }
        }
    }
}

// Defines various stages in the game.
enum class eGameStage{
    eStage1,
    eStage2,
    eStage3,
    eTotalGameStage;

    override fun toString(): String {
        return when(this){
            eStage1 ->"Stage 1"
            eStage2 ->"Stage 2"
            eStage3 ->"Stage 3"
            else->"Invalid Stage"
        }
    }
}