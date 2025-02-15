package com.example.gohero.enitities

object GameConstant{
    const val DEFAULT_LIVES = 5U
    const val DEFAULT_HERO_SPEED = 25U
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

enum class eDirection{
    eUP, eDOWN, eLEFT, eRIGHT;

    companion object{
        public fun fromValue(value : Int) : eDirection{
            return when(value){
                0->eUP
                1->eDOWN
                2->eLEFT
                3->eRIGHT
                else->throw IllegalArgumentException("Invalid argument ${value}")
            }
        }
    }
}