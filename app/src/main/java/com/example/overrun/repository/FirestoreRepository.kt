package com.example.overrun.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.example.overrun.enitities.GameViewModel

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // Function to save challenge completion status to Firestore
    fun saveChallengeCompletion(userId: String, levelChallenges: Map<String, Boolean>, characterChallenges: Map<String, Boolean>) {
        val userDocRef = firestore.collection("users").document(userId)

        // Save the data to Firestore
        userDocRef.set(
            hashMapOf(
                "levelChallenges" to levelChallenges,
                "characterChallenges" to characterChallenges
            ), SetOptions.merge()
        ).addOnSuccessListener {
            Log.d("Firestore", "Challenges saved successfully")
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error saving challenges", e)
        }
    }

    // Function to load challenge data from Firestore (if needed)
    fun loadChallenges(userId: String, callback: (Map<String, Boolean>, Map<String, Boolean>) -> Unit) {
        val userDocRef = firestore.collection("users").document(userId)

        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val levelChallenges = document.get("levelChallenges") as Map<String, Boolean>
                val characterChallenges = document.get("characterChallenges") as Map<String, Boolean>
                callback(levelChallenges, characterChallenges)
            }
        }
    }
}
