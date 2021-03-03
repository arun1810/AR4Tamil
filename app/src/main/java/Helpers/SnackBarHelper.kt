package Helpers

import android.app.Activity

import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class SnackBarHelper private constructor() {

    val background_color: Int = -0x403f3f40

    private var messageSnackbar: Snackbar? = null
    private var lastMessage = ""

    private enum class dismissBehaviour {
        HIDE,
        SHOW,
        FINISH
    }

    companion object {
        private var instance: SnackBarHelper = SnackBarHelper()
    fun getInstance(): SnackBarHelper {
        return instance
    }
}
   fun isShowing() = messageSnackbar !=null

   fun showmessage(activity: Activity, msg: String){
        if(!msg.isEmpty() && (!isShowing() || !lastMessage.equals(msg))){
            lastMessage = msg
            show(activity,msg,dismissBehaviour.HIDE)
        }
    }

    fun showMessageWithDismiss(activity: Activity,msg: String) = show(activity,msg,dismissBehaviour.SHOW)

    fun showError(activity: Activity,errormsg: String) = show(activity,errormsg,dismissBehaviour.FINISH)

    fun hide(activity: Activity){
        if(!isShowing()){
            return;
        }
        lastMessage=""
        activity.runOnUiThread(Runnable {
            messageSnackbar?.dismiss()
            messageSnackbar=null
        })
    }

    private fun show(activity: Activity, msg: String, DismissBehaviour: dismissBehaviour){
       activity.runOnUiThread(Runnable {
           messageSnackbar = Snackbar.make(
               activity.findViewById(android.R.id.content),
               msg,
               Snackbar.LENGTH_INDEFINITE
           )

           messageSnackbar!!.view.setBackgroundColor(background_color)

           if (DismissBehaviour != dismissBehaviour.HIDE) {
               //messageSnackbar!!.setAction()
               messageSnackbar!!.setAction("Dismiss",
                   {messageSnackbar!!.dismiss() } )

               if (DismissBehaviour != dismissBehaviour.FINISH) {
                   messageSnackbar!!.addCallback(object:
                       BaseTransientBottomBar.BaseCallback<Snackbar>() {
                       override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                           super.onDismissed(transientBottomBar, event)
                           activity.finish()
                       }
                   })
               }
           }


       })
       messageSnackbar!!.show()
   }
}