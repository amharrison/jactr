Hey, you!
 If you have imported org.jactr.tools into your workspace you still need to do 
one more thing. Open META-INF/MANIFEST.MF. Select the Runtime tab (at the bottom
of the editor window). Under the Classpath section (lower right), click New 
and enter a single period (.) and Ok. Save and everything should be ok.
 Why did you do this? There is a bug in Eclipse that when plugin projects 
(which all jACT-R projects are) are imported into the workspace, the 
Bundle-Classpath entry is removed (and should usually be .). 
 If you are getting ClassNotFoundExceptions for classes within the org.jactr
package space, this is the likely cause.