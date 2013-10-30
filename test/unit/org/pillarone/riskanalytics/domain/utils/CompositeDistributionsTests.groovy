package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.probdist.NormalDist
import umontreal.iro.lecuyer.probdist.ExponentialDist
import umontreal.iro.lecuyer.probdist.ParetoDist
import umontreal.iro.lecuyer.probdist.LognormalDist

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class CompositeDistributionsTests extends GroovyTestCase {

    void testGeneralizedParetoForXiEqualsZero() {

            double zeta = 1
            double beta = 0
            double xi = 0

            assertEquals "f(x = 1)", ExponentialDist.density(1.0, 1.0), GeneralizedParetoDistribution.density(xi, beta, zeta, 1.0)
            assertEquals "F(x = 1)", ExponentialDist.cdf(1.0, 1.0), GeneralizedParetoDistribution.cdf(xi, beta, zeta, 1.0)
            assertEquals "barF(x = 1)", ExponentialDist.barF(1.0, 1.0), GeneralizedParetoDistribution.barF(xi, beta, zeta, 1.0)
            assertEquals "inverseF(y = 0.9)", ExponentialDist.inverseF(1.0, 0.9), GeneralizedParetoDistribution.inverseF(xi, beta, zeta, 0.9)

            beta = -1

            assertEquals "f(x = 1)", ExponentialDist.density(1.0, 2.0), GeneralizedParetoDistribution.density(xi, beta, zeta, 1.0)
            assertEquals "F(x = 1)", ExponentialDist.cdf(1.0, 2.0), GeneralizedParetoDistribution.cdf(xi, beta, zeta, 1.0)
            assertEquals "barF(x = 1)", ExponentialDist.barF(1.0, 2.0), GeneralizedParetoDistribution.barF(xi, beta, zeta, 1.0)
            assertEquals "inverseF(y = 0.9)", ExponentialDist.inverseF(1.0, 0.9) - 1, GeneralizedParetoDistribution.inverseF(xi, beta, zeta, 0.9)
        }

        void testGeneralizedParetoForXiGreaterZero() {

            double zeta = 1
            double beta = 2
            double xi = 0.5
            double alpha = 2

            assertEquals "f(x = 3)", ParetoDist.density(alpha, beta, 3), GeneralizedParetoDistribution.density(xi, beta, zeta, 3.0)
            assertEquals "F(x = 3)", ParetoDist.cdf(alpha, beta, 3), GeneralizedParetoDistribution.cdf(xi, beta, zeta, 3.0)
            assertEquals "barF(x = 3)", ParetoDist.barF(alpha, beta, 3), GeneralizedParetoDistribution.barF(xi, beta, zeta, 3.0)
            assertEquals "inverseF(y=0.9)", ParetoDist.inverseF(alpha, beta, 0.9), GeneralizedParetoDistribution.inverseF(xi, beta, zeta, 0.9), 1E-8

            beta = 0
            xi = 2

            assertEquals "f(x = 1)", Math.pow(3, -1.5), GeneralizedParetoDistribution.density(xi, beta, zeta, 1.0)
            assertEquals "F(x = 1)", 1 - Math.pow(3, -0.5), GeneralizedParetoDistribution.cdf(xi, beta, zeta, 1.0)
            assertEquals "barF(x = 1)", Math.pow(3, -0.5), GeneralizedParetoDistribution.barF(xi, beta, zeta, 1.0)
            assertEquals "inverseF(y = 0.9)", -1 / 2d * (1d - Math.pow(0.1, -2)), GeneralizedParetoDistribution.inverseF(xi, beta, zeta, 0.9), 1E-8
        }

        void testGeneralizedParetoForXiSmallerZero() {

            double zeta = 1
            double beta = -1
            double xi = -1

            assertEquals "f(x = -0.5)", 1.0, GeneralizedParetoDistribution.density(xi, beta, zeta, -0.5)
            assertEquals "F(x = -0.5)", 0.5, GeneralizedParetoDistribution.cdf(xi, beta, zeta, -0.5)
            assertEquals "barF(x = -0.5)", 0.5, GeneralizedParetoDistribution.barF(xi, beta, zeta, -0.5)
            assertEquals "inverseF(y=0.9)", -0.1, GeneralizedParetoDistribution.inverseF(xi, beta, zeta, 0.9), 1E-8

            beta = -1
            xi = -2

            assertEquals "f(x = -0.5)", 0.0, GeneralizedParetoDistribution.density(xi, beta, zeta, -0.5)
            assertEquals "F(x =-0.5)", 1.0, GeneralizedParetoDistribution.cdf(xi, beta, zeta, -0.5)
            assertEquals "barF(x = -0.5)", 0.0, GeneralizedParetoDistribution.barF(xi, beta, zeta, -0.5)

            assertEquals "f(x = -0.75)", Math.pow(1 - 2 * (-0.75 + 1), -0.5), GeneralizedParetoDistribution.density(xi, beta, zeta, -0.75)
            assertEquals "F(x = -0.75)", 1 - Math.pow(1 - 2 * (-0.75 + 1), 0.5), GeneralizedParetoDistribution.cdf(xi, beta, zeta, -0.75)
            assertEquals "barF(x = -0.75)", Math.pow(1 - 2 * (-0.75 + 1), 0.5), GeneralizedParetoDistribution.barF(xi, beta, zeta, -0.75)
            assertEquals "inverseF(y=0.9)", -1 + 1 / 2 * (1 - Math.pow(1 - 0.9, 2)), GeneralizedParetoDistribution.inverseF(xi, beta, zeta, 0.9), 1E-8

            beta = 0
            xi = -0.5

            assertEquals "f(x = 2)", 0.0, GeneralizedParetoDistribution.density(xi, beta, zeta, 2)
            assertEquals "F(x =2)", 1.0, GeneralizedParetoDistribution.cdf(xi, beta, zeta, 2)
            assertEquals "barF(x = 2)", 0.0, GeneralizedParetoDistribution.barF(xi, beta, zeta, 2)

            assertEquals "f(x = 1.5)", Math.pow(1 - 1 / 2d * (1.5), 1.0), GeneralizedParetoDistribution.density(xi, beta, zeta, 1.5)
            assertEquals "F(x = 1.5)", 1 - Math.pow(1 - 1 / 2 * 1.5, 2), GeneralizedParetoDistribution.cdf(xi, beta, zeta, 1.5)
            assertEquals "barF(x = 1.5)", Math.pow(1 - 1 / 2 * 1.5, 2), GeneralizedParetoDistribution.barF(xi, beta, zeta, 1.5)
            assertEquals "inverseF(y=0.9)", 2 * (1 - Math.pow(1 - 0.9, 1 / 2d)), GeneralizedParetoDistribution.inverseF(xi, beta, zeta, 0.9), 1E-8
        }

        void testTypeIIPareto() {

            double zeta = 10
            double alpha = 2
            double beta = 5
            double xi = 1 / 2d
            double lambda = alpha * zeta - beta

            assertEquals "f(x = 7)", GeneralizedParetoDistribution.density(xi, beta, zeta, 7), TypeIIParetoDistribution.density(alpha, beta, lambda, 7)
            assertEquals "F(x = 7)", GeneralizedParetoDistribution.cdf(xi, beta, zeta, 7), TypeIIParetoDistribution.cdf(alpha, beta, lambda, 7)
            assertEquals "barF(x = 7)", GeneralizedParetoDistribution.barF(xi, beta, zeta, 7), TypeIIParetoDistribution.barF(alpha, beta, lambda, 7)
            assertEquals "inverseF(y=0.9)", GeneralizedParetoDistribution.inverseF(xi, beta, zeta, 0.9), TypeIIParetoDistribution.inverseF(alpha, beta, lambda, 0.9), 1E-8
        }

        void testLognormalPareto() {

            double alpha = 2
            double beta = 10
            double sigma = 1

            double term = Math.sqrt(2d * Math.PI) * alpha * sigma * NormalDist.cdf(0, 1, alpha * sigma) * Math.exp(1 / 2d * Math.pow(alpha * sigma, 2))
            double r = term / (1.0 + term)
            double mu = Math.log(beta) - alpha * Math.pow(sigma, 2)
            double normalizationConstant = NormalDist.cdf(mu, sigma, Math.log(beta))

            assertEquals "f(x = 7)", r / normalizationConstant * LognormalDist.density(mu, sigma, 7), LognormalParetoDistribution.density(sigma, alpha, beta, 7)
            assertEquals "F(x = 7)", r / normalizationConstant * LognormalDist.cdf(mu, sigma, 7), LognormalParetoDistribution.cdf(sigma, alpha, beta, 7)
            assertEquals "barF(x = 7)", 1.0 - r / normalizationConstant * LognormalDist.cdf(mu, sigma, 7), LognormalParetoDistribution.barF(sigma, alpha, beta, 7)
            assertEquals "inverseF(y=0.9)", LognormalDist.inverseF(mu, sigma, 0.9 * normalizationConstant / r), LognormalParetoDistribution.inverseF(sigma, alpha, beta, 0.9)

            assertEquals "f(x = 12)", (1.0 - r) * ParetoDist.density(alpha, beta, 12), LognormalParetoDistribution.density(sigma, alpha, beta, 12)
            assertEquals "F(x = 12)", r + (1.0 - r) * ParetoDist.cdf(alpha, beta, 12), LognormalParetoDistribution.cdf(sigma, alpha, beta, 12)
            assertEquals "barF(x = 12)", 1.0 - (r + (1.0 - r) * ParetoDist.cdf(alpha, beta, 12)), LognormalParetoDistribution.barF(sigma, alpha, beta, 12)
            assertEquals "inverseF(y=0.98)", ParetoDist.inverseF(alpha, beta, (0.98 - r) / (1 - r)), LognormalParetoDistribution.inverseF(sigma, alpha, beta, 0.98)

        }

        void testLognormalTypeIIPareto() {

            double alpha = 2
            double beta = 10
            double sigma = 0.5
            double lambda = 0

            assertEquals "f(x = 7)", LognormalParetoDistribution.density(sigma, alpha, beta, 7), LognormalTypeIIParetoDistribution.density(sigma, alpha, beta, lambda, 7)
            assertEquals "F(x = 7)", LognormalParetoDistribution.cdf(sigma, alpha, beta, 7), LognormalTypeIIParetoDistribution.cdf(sigma, alpha, beta, lambda, 7)
            assertEquals "barF(x = 7)", LognormalParetoDistribution.barF(sigma, alpha, beta, 7), LognormalTypeIIParetoDistribution.barF(sigma, alpha, beta, lambda, 7)
            assertEquals "inverseF(y=0.5)", LognormalParetoDistribution.inverseF(sigma, alpha, beta, 0.5), LognormalTypeIIParetoDistribution.inverseF(sigma, alpha, beta, lambda, 0.5)

            assertEquals "f(x = 12)", LognormalParetoDistribution.density(sigma, alpha, beta, 12), LognormalTypeIIParetoDistribution.density(sigma, alpha, beta, lambda, 12)
            assertEquals "F(x = 12)", LognormalParetoDistribution.cdf(sigma, alpha, beta, 12), LognormalTypeIIParetoDistribution.cdf(sigma, alpha, beta, lambda, 12)
            assertEquals "barF(x = 12)", LognormalParetoDistribution.barF(sigma, alpha, beta, 12), LognormalTypeIIParetoDistribution.barF(sigma, alpha, beta, lambda, 12)
            assertEquals "inverseF(y=0.98)", LognormalParetoDistribution.inverseF(sigma, alpha, beta, 0.98), LognormalTypeIIParetoDistribution.inverseF(sigma, alpha, beta, lambda, 0.98), 1E-8

            lambda = -5
            double mu = Math.log(beta) - ((alpha * beta - lambda) / (lambda + beta)) * Math.pow(sigma, 2)
            double term = Math.sqrt(2d * Math.PI) * alpha * beta * sigma * NormalDist.cdf(mu, sigma, Math.log(beta)) * Math.exp(1 / 2d * Math.pow((Math.log(beta) - mu) / sigma, 2))
            double r = term / (lambda + beta + term)
            double normalizationConstant = NormalDist.cdf(mu, sigma, Math.log(beta))

            assertEquals "f(x = 5)", r / normalizationConstant * LognormalDist.density(mu, sigma, 5), LognormalTypeIIParetoDistribution.density(sigma, alpha, beta, lambda, 5)
            assertEquals "F(x = 5)", r / normalizationConstant * LognormalDist.cdf(mu, sigma, 5), LognormalTypeIIParetoDistribution.cdf(sigma, alpha, beta, lambda, 5)
            assertEquals "barF(x = 5)", 1.0 - r / normalizationConstant * LognormalDist.cdf(mu, sigma, 5), LognormalTypeIIParetoDistribution.barF(sigma, alpha, beta, lambda, 5)
            assertEquals "inverseF(y=0.5)", LognormalDist.inverseF(mu, sigma, 0.5 * normalizationConstant / r), LognormalTypeIIParetoDistribution.inverseF(sigma, alpha, beta, lambda, 0.5)

            assertEquals "f(x = 11)", (1.0 - r) * TypeIIParetoDistribution.density(alpha, beta, lambda, 11), LognormalTypeIIParetoDistribution.density(sigma, alpha, beta, lambda, 11)
            assertEquals "F(x = 11", r + (1.0 - r) * TypeIIParetoDistribution.cdf(alpha, beta, lambda, 11), LognormalTypeIIParetoDistribution.cdf(sigma, alpha, beta, lambda, 11)
            assertEquals "barF(x = 11)", 1.0 - (r + (1.0 - r) * TypeIIParetoDistribution.cdf(alpha, beta, lambda, 11)), LognormalTypeIIParetoDistribution.barF(sigma, alpha, beta, lambda, 11)
            assertEquals "inverseF(y=0.995)", TypeIIParetoDistribution.inverseF(alpha, beta, lambda, (0.995 - r) / (1.0 - r)), LognormalTypeIIParetoDistribution.inverseF(sigma, alpha, beta, lambda, 0.995)

        }
    }
