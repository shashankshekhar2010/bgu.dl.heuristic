(define (problem BLOCKS-4-0)
(:domain BLOCKS)
(:objects D B A C )
(:init (ontable d)(clear d)(ontable a)(clear c)(ontable c)(clear b)(handempty)(on b a))
(:goal (AND (ON D C) (ON C B) (ON B A)))
)
