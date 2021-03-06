/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.empublite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class NoteFragment extends SherlockFragment implements
    DatabaseHelper.NoteListener {
  private static final String KEY_POSITION="position";
  private EditText editor=null;
  private boolean isDeleted=false;

  static NoteFragment newInstance(int position) {
    NoteFragment frag=new NoteFragment();
    Bundle args=new Bundle();

    args.putInt(KEY_POSITION, position);
    frag.setArguments(args);

    return(frag);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.editor, container, false);
    int position=getArguments().getInt(KEY_POSITION, -1);

    editor=(EditText)result.findViewById(R.id.editor);
    DatabaseHelper.getInstance(getActivity()).getNoteAsync(position,
                                                           this);

    setHasOptionsMenu(true);

    return(result);
  }

  @Override
  public void onPause() {
    if (!isDeleted) {
      int position=getArguments().getInt(KEY_POSITION, -1);

      DatabaseHelper.getInstance(getActivity())
                    .saveNoteAsync(position,
                                   editor.getText().toString());
    }

    super.onPause();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.notes, menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.delete) {
      int position=getArguments().getInt(KEY_POSITION, -1);

      isDeleted=true;
      DatabaseHelper.getInstance(getActivity())
                    .deleteNoteAsync(position);

      ((NoteListener)getActivity()).closeNotes();

      return(true);
    }
    else if (item.getItemId() == R.id.share) {
      NoteActivity.sendNotes(getActivity(), editor.getText().toString());

      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void setNote(String note) {
    editor.setText(note);
  }
  
  interface NoteListener {
    void closeNotes();
  }
}