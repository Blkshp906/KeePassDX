/*
 * Copyright 2018 Jeremy Jamet / Kunzisoft.
 *
 * This file is part of KeePass DX.
 *
 *  KeePass DX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  KeePass DX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with KeePass DX.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.kunzisoft.keepass.settings.preferenceDialogFragment;

import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kunzisoft.keepass.R;
import com.kunzisoft.keepass.settings.preference.InputNumberPreference;

public class RoundsFixPreferenceDialogFragmentCompat extends InputPreferenceDialogFragmentCompat {

    private EditText inputTextView;

    public static RoundsFixPreferenceDialogFragmentCompat newInstance(
            String key) {
        final RoundsFixPreferenceDialogFragmentCompat
                fragment = new RoundsFixPreferenceDialogFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        inputTextView = view.findViewById(R.id.input_text);

        DialogPreference preference = getPreference();
        if (preference instanceof InputNumberPreference) {
            setExplanationText(((InputNumberPreference) preference).getExplanation());
            long numRounds = ((InputNumberPreference) preference).getNumber();
            setInputText(String.valueOf(numRounds));
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if ( positiveResult ) {
            long rounds;
            try {
                String strRounds = getInputText();
                rounds = Long.valueOf(strRounds);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), R.string.error_rounds_not_number, Toast.LENGTH_LONG).show();
                return;
            }

            DialogPreference preference = getPreference();
            if (preference instanceof InputNumberPreference) {
                InputNumberPreference roundsPreference = (InputNumberPreference) preference;
                // This allows the client to ignore the user value.
                if (roundsPreference.callChangeListener(rounds)) {
                    // Save the value
                    roundsPreference.setNumber(rounds);
                }
            }
        }
    }

    public String getInputText() {
        return this.inputTextView.getText().toString();
    }

    public void setInputText(String inputText) {
        this.inputTextView.setText(inputText);
        this.inputTextView.setSelection(this.inputTextView.getText().length());
    }
}
