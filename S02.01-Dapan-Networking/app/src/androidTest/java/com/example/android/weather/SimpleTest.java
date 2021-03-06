/ * 
   *   C o p y r i g h t   ( C )   2 0 1 6   T h e   A n d r o i d   O p e n   S o u r c e   P r o j e c t 
   * 
   *   L i c e n s e d   u n d e r   t h e   A p a c h e   L i c e n s e ,   V e r s i o n   2 . 0   ( t h e   " L i c e n s e " ) ; 
   *   y o u   m a y   n o t   u s e   t h i s   f i l e   e x c e p t   i n   c o m p l i a n c e   w i t h   t h e   L i c e n s e . 
   *   Y o u   m a y   o b t a i n   a   c o p y   o f   t h e   L i c e n s e   a t 
   * 
   *             h t t p : / / w w w . a p a c h e . o r g / l i c e n s e s / L I C E N S E - 2 . 0 
   * 
   *   U n l e s s   r e q u i r e d   b y   a p p l i c a b l e   l a w   o r   a g r e e d   t o   i n   w r i t i n g ,   s o f t w a r e 
   *   d i s t r i b u t e d   u n d e r   t h e   L i c e n s e   i s   d i s t r i b u t e d   o n   a n   " A S   I S "   B A S I S , 
   *   W I T H O U T   W A R R A N T I E S   O R   C O N D I T I O N S   O F   A N Y   K I N D ,   e i t h e r   e x p r e s s   o r   i m p l i e d . 
   *   S e e   t h e   L i c e n s e   f o r   t h e   s p e c i f i c   l a n g u a g e   g o v e r n i n g   p e r m i s s i o n s   a n d 
   *   l i m i t a t i o n s   u n d e r   t h e   L i c e n s e . 
   * / 
 p a c k a g e   c o m . e x a m p l e . a n d r o i d . s u n s h i n e ; 
 
 i m p o r t   a n d r o i d . o s . B u i l d ; 
 i m p o r t   a n d r o i d . s u p p o r t . t e s t . r u n n e r . A n d r o i d J U n i t 4 ; 
 
 i m p o r t   o r g . j u n i t . T e s t ; 
 i m p o r t   o r g . j u n i t . r u n n e r . R u n W i t h ; 
 
 i m p o r t   s t a t i c   o r g . j u n i t . A s s e r t . a s s e r t T r u e ; 
 
 @ R u n W i t h ( A n d r o i d J U n i t 4 . c l a s s ) 
 p u b l i c   c l a s s   S i m p l e T e s t   { 
 
         / * * 
           *   V i �c   n � y   s �  k i �m   t r a   �  �m   b �o   r �n g   p h i � n   b �n   h i �n   t �i   a n g   c h �y   �n g   d �n g   s �
           *   l �n   h �n   G i n g e r b r e a d .   C h � n g   t � i   b i �t   i �u   n � y   s �  � n g   v �   c h � n g   t � i   �   c h �  �n h   S D K 
           *   t �i   t h i �u   c h o   �n g   d �n g   c �a   c h � n g   t � i   l �   A P I   1 5   v �   G i n g e r b r e a d   l �   A P I   c �p   9 , 
           *   n h �n g   t a   m u �n   c h �n g   m i n h   m �t   b � i   k i �m   t r a   �n   g i �n   r �t   d �  h i �u   v �   c h �y   t r � n   b �
           *   m �   p h �n g   A n d r o i d .   �  c h �y   t h �  n g h i �m   n � y ,   n h �p   c h u �t   p h �i   v � o   t �p   n � y   v �   n h �p   v � o 
           *   C h �y   ' t e s t A n d r o i d V e r s i o n   . . . . . '   M �c   �n h   l �   s �  p a s s .   �  k i �m   t r a   t h �t   b �i , 
           *   h � y   �i   d �u   ' < '   t h � n h   ' > ' . 
           * / 
         @ T e s t 
         p u b l i c   v o i d   t e s t A n d r o i d V e r s i o n G r e a t e r T h a n G i n g e r b r e a d ( )   { 
                 i n t   c u r r e n t A n d r o i d V e r s i o n   =   B u i l d . V E R S I O N . S D K _ I N T ; 
                 i n t   g i n g e r b r e a d   =   B u i l d . V E R S I O N _ C O D E S . G I N G E R B R E A D ; 
                 a s s e r t T r u e ( c u r r e n t A n d r o i d V e r s i o n   >   g i n g e r b r e a d ) ; 
         } 
 } 