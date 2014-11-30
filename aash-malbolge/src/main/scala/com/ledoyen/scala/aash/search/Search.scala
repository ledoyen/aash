package com.ledoyen.scala.aash.search

object Search {

  def binary_tree(x: Int) = List(2 * x, 2 * x + 1)

  /**
   * Return a successor function that generates a binary tree with n nodes.
   */
  def finite_binary_tree(n: Int) = (x: Int) => binary_tree(x).filterNot(_ > n)

  def is[T](x: T) = (l: T) => l.equals(x)

  def fail = throw new RuntimeException("Fail")

  /** Return the function that finds the difference from num. */
  def diff(num: Int) = (x: Int) => (num - x).abs

  /** Return a function that measures the difference from price, but gives a big penalty f o r going over price. */
  def price_is_right(num: Int) = (x: Int) => if(x > num) 15000 else num - x

  /** Return a combiner function that sorts according to cost-fn. */
  def sorter[T](cost_fn: T => Int) = (_new: List[T], old: List[T]) => (_new ::: old).sortWith((t1, t2) => cost_fn(t1) < cost_fn(t2))

  /** Find a state that satisfies goal-p.  Start with states, and search according to successors and combiner. */
  def tree_search[T](states: List[T], goal_p: T => Boolean, successors: T => List[T], combiner: (List[T], List[T]) => List[T]): T = {
    println(s";; Search: $states");
    if (states.length == 0) fail
    else if (goal_p(states.head)) states.head
    else tree_search(combiner(successors(states.head), states.tail), goal_p, successors, combiner)
  }

  /** Search new states first until goal is reached. */
  def depth_first_search[T](start: T, goal_p: T => Boolean, successors: T => List[T]) = tree_search(List(start), goal_p, successors, (l1: List[T], l2: List[T]) => l1 ::: l2)

  /** Search old states first until goal is reached. */
  def breadth_first_search[T](start: T, goal_p: T => Boolean, successors: T => List[T]) = tree_search(List(start), goal_p, successors, (l1: List[T], l2: List[T]) => l2 ::: l1)

  /** Search lowest cost states first until goal is reached. */
  def best_first_search[T](start: T, goal_p: T => Boolean, successors: T => List[T], cost_fn: T => Int) = tree_search(List(start), goal_p, successors, sorter(cost_fn))

  /** Search highest scoring states first until goal is reached, but never consider more than beam-width states at a time. */
  def beam_search[T](start: T, goal_p: T => Boolean, successors: T => List[T], cost_fn: T => Int, beam_width: Int) = {
    tree_search(List(start), goal_p, successors,
      (old: List[T], _new: List[T]) => {
        val sorted = sorter(cost_fn)(old, _new)
        if (beam_width > sorted.length) sorted else sorted.take(beam_width)
      })
  }
}