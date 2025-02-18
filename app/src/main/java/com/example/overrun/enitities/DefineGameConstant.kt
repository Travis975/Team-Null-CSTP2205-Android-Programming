package com.example.gohero.enitities

import androidx.annotation.DrawableRes
import com.example.overrun.R

object GameConstant{
    const val DEFAULT_LIVES = 5U
    const val DEFAULT_HERO_SPEED = 25U
    const val HERO_PIXEL_SIZE = 96
}

enum class eCharactertype{

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

enum class eHerotype(@DrawableRes val resId : Int){

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