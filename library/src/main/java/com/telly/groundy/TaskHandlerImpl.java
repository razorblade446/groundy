/**
 * Copyright Telly, Inc. and other Groundy contributors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.telly.groundy;

import android.content.Context;
import android.os.Parcel;

class TaskHandlerImpl implements TaskHandler {

  private final Groundy groundy;
  private boolean mTaskEnded = false;

  TaskHandlerImpl(Groundy groundy) {
    this.groundy = groundy;
  }

  @Override public void cancel(Context context, int reason,
      GroundyManager.SingleCancelListener cancelListener) {
    if (!mTaskEnded) {
      GroundyManager.cancelTaskById(context, groundy.getId(), reason, cancelListener,
          groundy.getGroundyServiceClass());
    } else {
      cancelListener.onCancelResult(groundy.getId(), GroundyService.COULD_NOT_CANCEL);
    }
  }

  @Override public void clearCallbacks() {
    CallbacksReceiver callbacksReceiver = groundy.getReceiver();
    if (callbacksReceiver != null) {
      callbacksReceiver.clearHandlers();
    }
  }

  @Override public boolean taskAlreadyEnded() {
    return mTaskEnded;
  }

  @Override public void appendCallbacks(Object... handlers) {
    CallbacksReceiver callbacksReceiver = groundy.getReceiver();
    callbacksReceiver.appendCallbackHandlers(handlers);
  }

  @Override public void removeCallbacks(Object... handlers) {
    CallbacksReceiver callbacksReceiver = groundy.getReceiver();
    callbacksReceiver.removeCallbackHandlers(groundy.getGroundyTaskClass(), handlers);
  }

  @Override public long getTaskId() {
    return groundy.getId();
  }

  void onTaskEnded() {
    mTaskEnded = true;
  }

  @SuppressWarnings("UnusedDeclaration")
  public static final Creator<TaskHandlerImpl> CREATOR = new Creator<TaskHandlerImpl>() {
    @Override public TaskHandlerImpl createFromParcel(Parcel source) {
      Groundy groundy = source.readParcelable(Groundy.class.getClassLoader());
      //noinspection unchecked
      return new TaskHandlerImpl(groundy);
    }

    @Override public TaskHandlerImpl[] newArray(int size) {
      return new TaskHandlerImpl[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(groundy, flags);
  }
}
