package com.example.overrun.enitities

import androidx.annotation.DrawableRes
import com.example.overrun.R

object GameConstant{
    // Hero Attribute
    // All value are in Pixels
    const val DEFAULT_LIVES = 5U
    const val DEFAULT_HERO_SPEED = 50U
    const val INTERACT_FILER_INTERVAL_MS = 100U

    // for scaling
    const val GAME_SCREEN_COL = 7f //7.5f
    const val GAME_SCREEN_ROW = 14f //15.3f
    const val DEFAULT_SCREEN_WIDTH_PIXEL = 1080U        // a Medium Phone API 35 width pixel
    const val DEFAULT_SCREEN_HEIGHT_PIXEL = 2211U       // a Medium Phone API 35 height pixel

    const val HERO_CHARACTER_SPRITE_WIDTH_PIXEL = 144U
    const val HERO_CHARACTER_SPRITE_HEIGHT_PIXEL = 144U

    const val DEFAULT_INTERACT_SIZE_EXTEND_RATIO = 1.4375f

    // For record, keep here
    //const val DEFAULT_OBJECT_SIZE = 144U // 96U // 144U    // x 9 from 16 pixels = 144U (Medium Phone),  x 6 from 16 pixels = 96U (nokia 2.4)
    //const val DEFAULT_CHARACTER_INTERACT_SIZE = 207U // 138U // 207U // x 9 from 23 pixels, ,  x 6 from 23 pixels (nokia 2.4)

    // Offset -ve : shrink the other box
    // Offset +ve : enlarge the other box
    const val MOVE_COLLIDE_OFFSET_X = -30
    const val MOVE_COLLIDE_OFFSET_Y = -30

    const val BE_INTERACT_COLLIDE_OFFSET_X = 30
    const val BE_INTERACT_COLLIDE_OFFSET_Y = 30
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

    eNA(-1),
    eGRASS(0),
    eTREE_BACKGROUND(1), eTREE(11),
    eROCK(2),
    eROCK_1(21),
    eROCK_TOXIC(22),
    eWALL(3),
    ePATH(4),
    eCHARACTER(99);

    public fun isStatic() : Boolean
    {
        return when(this){
            eGRASS, eTREE_BACKGROUND->true
            else->false
        }
    }

    // Able to be interact or able to interact hero
    public fun isInteractable() : Boolean
    {
        return when(this){
            eROCK_1, eROCK_TOXIC->true
            else->false
        }
    }

    // Once interact to hero happen, use the objectType to
    // check whether it is harmful to hero on reducing the life
    public fun isHarmful(): Boolean
    {
        return when(this){
            eROCK_TOXIC->true
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
            eCHARACTER->"Character"
            eTREE->"Tree"
            eTREE_BACKGROUND->"Tree_Background"
            eWALL->"Wall"
            eROCK->"Rock"
            ePATH->"Path"
            eGRASS->"Grass"
            else->"Invalid Object"
        }
    }
}

enum class eCharacterType{

    eNA, eHERO, eSLIME, ePARROT;

    override fun toString(): String {
        return when(this){
            eHERO->"Hero"
            eSLIME->"Slime"
            ePARROT->"Parrot"
            else->"Invalid Character"
        }
    }
}

enum class eHeroType(@DrawableRes val resId : Int){

    eHERO_TOKAGE(R.drawable.hero_tokage);

//    fun getResId() : Int
//    {
//        return resId
//    }
}

enum class eDirection(val value: Int){
    eDOWN(0), eUP(1), eLEFT(2), eRIGHT(3);

    companion object{
        public fun fromValue(value : Int) : eDirection{
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