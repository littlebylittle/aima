/****************************************************************/
/*    NAME: Konstantinos Roussos                                */
/*    ACCT: knr                                                 */
/*    FILE: Searches.H                                          */
/*    ASGN: Final                                               */
/*    DATE: Wed Jun 22 15:04:54 1994                            */
/****************************************************************/

/*
 * Copyright 1994, Brown University, Providence, RI
 * See end of file for full copyright information
 */


#ifndef SEARCHES_HEADER
#define SEARCHES_HEADER

class State;
class Comparator;
#include <SortedQueue.H>
State* best(State* current, State* goal, Comparator* heuristic);
State* bfs(State* current, State* goal);
State* dfs(State* current, State* goal);
State* dfs_fd(State* current, State* goal, int current_depth, 
              int fixed_depth);
State* ids(State* current,
           State* goal);


#endif

/*
 * Copyright 1994, Brown University, Providence, RI
 *
 * Permission to use and modify this software and its documentation for
 * any purpose other than its incorporation into a commercial product is
 * hereby granted without fee.  Permission to copy and distribute this
 * software and its documentation only for non-commercial use is also
 * granted without fee, provided, however, that the above copyright notice
 * appear in all copies, that both that copyright notice and this permission
 * notice appear in supporting documentation, that the name of Brown
 * University not be used in advertising or publicity pertaining to
 * distribution of the software without specific, written prior permission,
 * and that the person doing the distribution notify Brown University of
 * such distributions outside of his or her organization. Brown University
 * makes no representations about the suitability of this software for
 * any purpose.  It is provided "as is" without express or implied warranty.
 * Brown University requests notification of any modifications to this
 * software or its documentation.
 *
 * Send the following redistribution information:
 *
 *	Name:
 *	Organization:
 *	Address (postal and/or electronic):
 *
 * To:
 *	Software Librarian
 *	Computer Science Department, Box 1910
 *	Brown University
 *	Providence, RI 02912
 *
 *		or
 *
 *	brusd@cs.brown.edu
 *
 * We will acknowledge all electronic notifications.
 */

