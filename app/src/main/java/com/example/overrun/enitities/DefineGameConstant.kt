package com.example.gohero.enitities

import androidx.annotation.DrawableRes
import com.example.overrun.R

object GameConstant{
    // All value are in Pixels
    const val DEFAULT_LIVES = 5U
    const val DEFAULT_HERO_SPEED = 25U

    const val DEFAULT_OBJECT_SIZE = 144U
    const val DEFAULT_CHARACTER_SIZE = DEFAULT_OBJECT_SIZE

    // Offset -ve : shrink the other box
    // Offset +ve : enlarge the other box
    const val MOVE_COLLIDE_OFFSET_X = -10
    const val MOVE_COLLIDE_OFFSET_Y = -10
}

// ObjectType
//      |_ eCHARACTER (CharacterType)
//                    |_  eHero (HeroType)
//                          |_ eHero_Tokage
//                    |_  Slime
//                    |_  Parrot
//       |_ eTREE
//       |_ eWALL
//       |_ eROCK
//       |_ ePATH
//       |_ eGRASS

enum class eObjectType{

    eNA, eCHARACTER, eTREE, eWALL, eROCK, ePATH, eGRASS;

    override fun toString(): String {
        return when(this){
            eCHARACTER->"Character"
            eTREE->"Tree"
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