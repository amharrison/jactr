
#|Automatically generated model  |#


#|*Unsupported warning* : Extensions, full buffer spec, scriptables and proxies |#

(define-model visual
  
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; module class org.jactr.core.module.declarative.six.DefaultDeclarativeModule6
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  
  (module "org.jactr.core.module.declarative.six.DefaultDeclarativeModule6")
  
  #|module parameter   :ans 0   :blc 0   :pm nil   MaximumDifference -10   MaximumSimilarity 10   :mp 0   :pas 0  |#
  
  
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; module class org.jactr.core.module.procedural.six.DefaultProceduralModule6
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  
  (module "org.jactr.core.module.procedural.six.DefaultProceduralModule6")
  
  #|module parameter   :dat 0.05   ExpectedUtilityNoise 0   NumberOfProductionsFired 0  |#
  
  
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; module class org.jactr.core.module.retrieval.six.DefaultRetrievalModule6
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  
  (module "org.jactr.core.module.retrieval.six.DefaultRetrievalModule6")
  
  #|module parameter   EnableIndexedRetrievals nil   FINSTDurationTime 3.0   :lf 1   NumberOfFINSTs 4   :rt 0  |#
  
  
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; module class org.jactr.modules.pm.visual.six.DefaultVisualModule6
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  
  (module "org.jactr.modules.pm.visual.six.DefaultVisualModule6")
  
  #|module parameter   EnableStickyAttention nil   EnableStrictSynchronization t   EnableVisualBufferStuff nil   FINSTDurationTime 3   MovementTolerance 0.5   NewFINSTOnsetDurationTime 0.5   NumberOfFINSTs 4   VisualEncodingTimeEquationClass org.jactr.modules.pm.visual.six.DefaultEncodingTimeEquation   VisualFieldHeight 120   VisualFieldHorizontalResolution 160   VisualFieldVerticalResolution 120   VisualFieldWidth 160   VisualPersistenceDelay 0   VisualSearchTimeEquationClass org.jactr.modules.pm.visual.six.DefaultSearchTimeEquation  |#
  
  (extension "org.jactr.tools.masterslave.slave.SlaveExtension") 

#|Chunk-types and chunks |#

  
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; declarative memory container for chunks and chunk-types
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  

#|Productions |#

  
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; procedural memory contents
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  
    (p visual-search.1
       ?visual-location>
        state free 
        buffer empty 
       
       ?visual>
        state free 
        buffer empty 
       
     ==>
        +visual-location>
        isa visual-location
         nearest current 
        - :attended t 
        
    )
  
  
    (p visual-attend.2a
       =visual-location>
       isa visual-location
       
       ?visual>
        state free 
       - buffer full 
       
     ==>
        +visual>
        isa attend-to
         where =visual-location 
        
    )
  
  
    (p visual-search-failed.2b
       ?visual-location>
        state error 
       
       ?visual>
        buffer empty 
       
     ==>
        +visual>
        isa clear
        
    )
  
  
    (p visual-attend-failed.3
       ?visual>
        state error 
       
     ==>
        +visual>
        isa clear
        
    )
  
  
    
    #|buffer parameter   Activation 0   G 0   StrictHarvestingEnabled t  |#
    
    
    #|buffer parameter   Activation 0   G 0   StrictHarvestingEnabled t   Activation 0   G 0   StrictHarvestingEnabled t  |#
    
    
    #|buffer parameter   Activation 0   G 0   StrictHarvestingEnabled t   Activation 0   G 0   StrictHarvestingEnabled t  |#
    
) ;define-model
