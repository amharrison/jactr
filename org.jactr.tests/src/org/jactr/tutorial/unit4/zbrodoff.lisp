(defvar *trials*)
(defvar *results*)
(defvar *start-time*)
(defvar *block*)
(defvar *show-window*)

(defconstant *zbrodoff-control-data* '(1.84 2.46 2.82 1.21 1.45 1.42 1.14 1.21 1.17))

(defstruct trial block addend1 addend2 sum answer visible)
(defstruct response block addend correct time)

(defun present-trial (trial)
  (let ((window (open-exp-window "Alpha-arithmetic Experiment" :visible (trial-visible trial))))
      
    (add-text-to-exp-window :text (trial-addend1 trial) :x 100 :y 150 :width 25)
    (add-text-to-exp-window :text "+" :x 125 :y 150 :width 25)
    (add-text-to-exp-window :text (trial-addend2 trial) :x 150 :y 150 :width 25)
    (add-text-to-exp-window :text "=" :x 175 :y 150 :width 25)
    (add-text-to-exp-window :text (trial-sum trial) :x 200 :y 150 :width 25)
    
    (install-device window)
    
    (proc-display :clear t)    
    
    (setf *start-time* (get-time))
    
    window))


(defmethod rpm-window-key-event-handler ((win rpm-window) key)
  (let ((trial (pop *trials*)))
    (push (make-response :block (trial-block trial)
                         :addend (trial-addend2 trial)
                         :time (/ (- (get-time) *start-time*) 1000.0)
                         :correct (string-equal (trial-answer trial) (string key)))
          *results*)
    (when *trials*
      (present-trial (first *trials*)))))


(defun collect-responses (trial-count)
  (setf *results* nil)
  (let ((window (present-trial (first *trials*))))
    (if *actr-enabled-p* 
        (run (* 10 trial-count))
        
      (while (< (length *results*) trial-count)
        (allow-event-manager window)))))


(defun do-trial (addend1 addend2 sum answer &optional (visible (not *actr-enabled-p*)))
  (setf *show-window* visible)
  (setf *block* 1)
  (setf *trials* (list (construct-trial (list addend1 addend2 sum answer))))
  (collect-responses 1)
  (analyze-results))

(defun do-set (&optional (visible (not *actr-enabled-p*)))
  (setf *show-window* visible)
  (setf *block* 1)
  (setf *trials* (create-set))
  (collect-responses 24)
  (analyze-results))

(defun do-block (&optional (visible (not *actr-enabled-p*)))
  (setf *show-window* visible)
  (setf *block* 1)
  (setf *trials* nil)
  (dotimes (i 8)
    (setf *trials* (append *trials* (create-set))))
  (collect-responses 192)
  (analyze-results))

(defun do-experiment (&optional (visible (not *actr-enabled-p*)) (display t))
  (reset)
  (setf *show-window* visible)
  (setf *trials* nil)
  (dotimes (j 3)
    (setf *block* (+ j 1))
    (dotimes (i 8)
      (setf *trials* (append *trials* (create-set)))))
  (collect-responses 576)
  (analyze-results display))

(defun collect-data (n)
  (let ((results nil))
    (dotimes (i n)
      (push (do-experiment nil nil) results))
    
    (let ((rts (mapcar #'(lambda (x) (/ x (length results)))
                 (apply #'mapcar #'+ (mapcar #'first results))))
          (counts (mapcar #'(lambda (x) (truncate x (length results)))
                    (apply #'mapcar #'+ (mapcar #'second results)))))
      
      (correlation rts *zbrodoff-control-data*)
      (mean-deviation rts *zbrodoff-control-data*)
      
      (print-analysis rts counts '(1 2 3) '("2" "3" "4") '(64 64 64)))))

        
(defun analyze-results (&optional (display t))
  (let ((blocks (sort (remove-duplicates (mapcar #'response-block *results*)) #'<))
        (addends (sort (remove-duplicates (mapcar #'response-addend *results*) :test #'string-equal) #'string<))
        (counts nil)
        (rts nil)
        (total-counts nil))
    
    (setf total-counts (mapcar #'(lambda (x) 
                                   (/ (count x *results* 
                                             :key #'response-addend 
                                             :test #'string=)
                                      (length blocks)))
                         addends))
    
    (dolist (x blocks)
      (dolist (y addends)
        (let ((data (mapcar #'response-time
                      (remove-if-not #'(lambda (z)
                                         (and (response-correct z)
                                              (string= y (response-addend z))
                                              (= x (response-block z))))
                                     *results*))))
          (push (length data) counts)
          (push (/ (apply #'+ data) (max 1 (length data))) rts))))
    
    
    (when display
      (print-analysis (reverse rts) (reverse counts) blocks addends total-counts))
      
    (list (reverse rts) (reverse counts))))

    
(defun print-analysis (rts counts blocks addends total-counts)
  (format t "~%        ")
  (dotimes (addend (length addends))
    (format t " ~6@a (~2d)" (nth addend addends) (nth addend total-counts)))
  (dotimes (block (length blocks))
    (format t "~%Block ~2d" (nth block blocks))
    (dotimes (addend (length addends))
      (format t " ~6,3f (~2d)" (nth (+ addend (* block (length addends))) rts)
        (nth (+ addend (* block (length addends))) counts))))
  (terpri))
        
  
(defun create-set ()
  (permute-list (mapcar #'construct-trial 
                  '(("a" "2" "c" "k")("d" "2" "f" "k")
                    ("b" "3" "e" "k")("e" "3" "h" "k")
                    ("c" "4" "g" "k")("f" "4" "j" "k")
                    ("a" "2" "d" "d")("d" "2" "g" "d")
                    ("b" "3" "f" "d")("e" "3" "i" "d")
                    ("c" "4" "h" "d")("f" "4" "k" "d")
                    ("a" "2" "c" "k")("d" "2" "f" "k")
                    ("b" "3" "e" "k")("e" "3" "h" "k")
                    ("c" "4" "g" "k")("f" "4" "j" "k")
                    ("a" "2" "d" "d")("d" "2" "g" "d")
                    ("b" "3" "f" "d")("e" "3" "i" "d")
                    ("c" "4" "h" "d")("f" "4" "k" "d")))))

(defun construct-trial (settings)
  (make-trial :block *block* 
              :addend1 (first settings)
              :addend2 (second settings)
              :sum (third settings)
              :answer (fourth settings)
              :visible *show-window*))
  

(clear-all)

(define-model zbrodoff
    
(sgp :v nil :esc t :lf 0.35 :bll 0.5 :ans 0.5 :rt 0)

(sgp :show-focus t)

(chunk-type problem arg1 arg2 ans count result state)
(chunk-type sequence identity next)

(add-dm
 (zero  ISA sequence identity "0" next "1")
 (one   ISA sequence identity "1" next "2")
 (two   ISA sequence identity "2" next "3")
 (three ISA sequence identity "3" next "4")
 (four  ISA sequence identity "4" next "5")
 (a ISA sequence identity "a" next "b")
 (b ISA sequence identity "b" next "c")
 (c ISA sequence identity "c" next "d")
 (d ISA sequence identity "d" next "e")
 (e ISA sequence identity "e" next "f")
 (f ISA sequence identity "f" next "g")
 (g ISA sequence identity "g" next "h")
 (h ISA sequence identity "h" next "i")
 (i ISA sequence identity "i" next "j")
 (j ISA sequence identity "j" next "k")
 (goal isa problem))

(P attend
   =goal>
      ISA         problem
      state       nil
   =visual-location>
      ISA         visual-location
   ?visual>
       state      free
==>
   =goal>
      state       attending
   +visual>
      ISA         move-attention
      screen-pos  =visual-location
)

(P read-first
   =goal>
     ISA         problem
     state       attending
     arg1        nil
   =visual>
     ISA         text
     value       =char
     status      nil
   ?vocal>
       state      free
==>
   +vocal>
     isa         subvocalize
     string      =char
   
   =goal>
     arg1        =char
     state       nil
   +visual-location>
     ISA         visual-location
   > screen-x    150 
   < screen-x    160
)


(P read-second
   =goal>
     ISA         problem
     state       attending
     arg1        =val
     arg2        nil
   =visual>
     ISA         text
     value       =char
     status      nil
   ?vocal>
       state      free
==>
   +vocal>
     isa         subvocalize
     string      =char
   =goal>
     arg2        =char
     state       nil
   +visual-location>
     ISA         visual-location
   > screen-x    200 
   < screen-x    210
)


(P read-third
   =goal>
     ISA         problem
     state       attending
     arg1        =a
     arg2        =val
     ans         nil
   =visual>
     ISA         text
     value       =char
     status      nil
   ?vocal>
       state      free
==>
   +vocal>
     isa         subvocalize
     string      =char
   =goal>
     ans         =char
     state       count
)


(P start-counting
   =goal>
     ISA         problem
     arg1        =a
     arg2        =val
     state       count
 
   ?vocal>
       state      free
==>
   +vocal>
     isa         subvocalize
     string      =a
   =goal>
     count       "0"
     result      =a
     state       counting
   +retrieval>
     ISA         sequence
     identity    =a
)


(P increment-count
   =goal>
     ISA         problem
     result      =ans
     count       =val
   - arg2        =val
   =retrieval>
     ISA         sequence
     identity    =val
     next        =new
   ?vocal>
       state      free
==>
   +vocal>
     isa         subvocalize
     string      =ans
   =goal>
     count       =new
   +retrieval>
     ISA         sequence
     identity    =ans
)


(P increment-result
   =goal>
     ISA         problem
     result      =let
     count       =val
   - arg2        =val
   =retrieval>
     ISA         sequence
     identity    =let
     next        =new
   ?vocal>
       state      free
==>
   +vocal>
     isa         subvocalize
     string      =val
   =goal>
     result      =new
   +retrieval>
     ISA         sequence
     identity    =val
)


(P final-answer-yes
   =goal>
     ISA         problem
     result      =let
     ans         =let
     count       =val
     arg2        =val
   ?vocal>
       state      free
   
   ?manual>
       state      free
   ==>
   =goal>
     ans         nil
     state       nil
   +goal>
     ISA         problem
   +manual>
     ISA         press-key
     key         "k"
   
)

(P final-answer-no
    =goal>
     ISA         problem
     result      =let
   - ans         =let
     count       =val
     arg2        =val
   ?vocal>
       state      free
   
   ?manual>
       state      free
   ==>
   =goal>
      ans         nil
      state       nil
   +goal>
     ISA         problem
   +manual>
     ISA         press-key
     key         "d"
   
)


(setf *actr-enabled-p* t)

(set-all-base-levels 10000000 -1000)
(goal-focus goal)

)
