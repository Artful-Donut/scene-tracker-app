package com.example.nguyen_project3;

import androidx.annotation.NonNull;

public enum TIME {
    EARLY_MORNING {
        @NonNull
        @Override
        public String toString()
        {
            return "Early Morning";
        }
    },
    MORNING {
        @NonNull
        @Override
        public String toString()
        {
            return "Morning";
        }
    },
    AFTERNOON {
        @NonNull
        @Override
        public String toString()
        {
            return "Afternoon";
        }
    },
    EARLY_AFTERNOON {
        @NonNull
        @Override
        public String toString()
        {
            return "Early Afternoon";
        }
    },
    EVENING {
        @NonNull
        @Override
        public String toString()
        {
            return "Evening";
        }
    },
    NIGHTTIME {
        @NonNull
        @Override
        public String toString()
        {
            return "Night";
        }
    },
    MIDNIGHT {
        @NonNull
        @Override
        public String toString()
        {
            return "Midnight";
        }
    }
}

