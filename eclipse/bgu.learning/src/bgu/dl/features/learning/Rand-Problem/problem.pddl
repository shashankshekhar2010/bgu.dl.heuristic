(define (problem BLOCKS-10-0)
(:domain BLOCKS)
(:objects  A B C D E F G H I J)
(:init (HANDEMPTY)(ON A E)(CLEAR A)(ON B D)(CLEAR B)(ONTABLE C)(ONTABLE D)(ONTABLE E)(ON F J)(CLEAR F)(ON G C)(CLEAR G)(ONTABLE H)(ON I H)(CLEAR I)(ONTABLE J))
(:goal (AND (HANDEMPTY)(ONTABLE A)(ONTABLE B)(ON C H)(ON D A)(ONTABLE E)(CLEAR E)(ON F I)(CLEAR F)(ON G J)(CLEAR G)(ON H D)(ON I C)(ON J B)))
)
